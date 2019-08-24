package dev.harmeetsingh.caradverts.repository.impl

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import akka.Done
import akka.actor.ActorSystem
import com.amazonaws.services.dynamodbv2.model.{AttributeDefinition, CreateTableRequest, KeySchemaElement, KeyType, ProvisionedThroughput, ScalarAttributeType}
import com.gu.scanamo.{DynamoFormat, ScanamoAlpakka, Table}
import dev.harmeetsingh.caradverts.configuration.dependencies.db.DynamoDBClient
import dev.harmeetsingh.caradverts.entity.Car._
import dev.harmeetsingh.caradverts.repository.CarRepo
import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.stream.alpakka.dynamodb.scaladsl.DynamoImplicits._
import dev.harmeetsingh.caradverts.entity.Car
import dev.harmeetsingh.caradverts.repository.impl.DynamoUtils._
import dev.harmeetsingh.caradverts.repository.impl.CarRepoImpl._
import dev.harmeetsingh.caradverts.model.EnumProtocols.ordering
import com.gu.scanamo.syntax._

object CarRepoImpl{
    private final val datePattern = DateTimeFormatter.ISO_LOCAL_DATE
    
    implicit val localDataFormat = DynamoFormat.coercedXmap[LocalDate, String, IllegalArgumentException](
        LocalDate.parse(_, datePattern)
    )(
        _.toString
    )
    
    final val CarTable: Table[Car] = Table[Car](TableName)
}
class CarRepoImpl (dynamoDBClient: DynamoDBClient)(implicit system: ActorSystem) extends CarRepo {
    
    implicit val m_executionContext : ExecutionContextExecutor = system.dispatcher
    val dbClient = dynamoDBClient.dbClient
    
    def createTable : Future[Done] = {
        val createTableRequest = new CreateTableRequest()
            .withTableName(TableName)
            .withKeySchema(
                new KeySchemaElement().withAttributeName(ID.name).withKeyType(KeyType.HASH)
            )
            .withAttributeDefinitions(
                new AttributeDefinition().withAttributeName(ID.name).withAttributeType(ScalarAttributeType.N)
            )
            .withProvisionedThroughput(
                new ProvisionedThroughput(10L, 10L)
            )
    
        dbClient
            .single(createTableRequest)
            .map(_ => Done)
    }
    
    def findAllCar(sortingField : Option[String]) : Future[List[Car]] = {
        val data = futureOfIterableEitherToFutureSeq (ScanamoAlpakka.exec(dbClient)(CarTable.scan())).map(_.toList)
        sortingField match{
            case Some("title") => data.map(_.sortBy(_.title))
            case Some("fuel") => data.map(_.sortBy(_.fuel))
            case Some("price") => data.map(_.sortBy(_.price))
            case Some("new") => data.map(_.sortBy(_.`new`))
            case Some("mileage") => data.map(_.sortBy(_.mileage))
            case _ => data.map(_.sortBy(_.id))
        }
    }
    
    def findCarByID(id: Int): Future[Option[Car]] = futureOfOptionEitherToFutureOption{
        ScanamoAlpakka.exec(dbClient)(CarTable.get(ID -> id))
    }
    
    def addNewCar(car: Car): Future[Option[Car]] = futureOfOptionEitherToFutureOption {
        ScanamoAlpakka.exec(dbClient)(CarTable.put(car))
    }
}
