package dev.harmeetsingh.caradverts.repository.impl

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import akka.Done
import akka.actor.ActorSystem
import akka.stream.alpakka.dynamodb.scaladsl.DynamoImplicits._
import com.amazonaws.services.dynamodbv2.model.{AttributeDefinition, CreateTableRequest, KeySchemaElement, KeyType, ProvisionedThroughput, ScalarAttributeType}
import com.gu.scanamo.syntax._
import com.gu.scanamo.{DynamoFormat, ScanamoAlpakka, Table}
import dev.harmeetsingh.caradverts.configuration.dependencies.db.DynamoDBClient
import dev.harmeetsingh.caradverts.entity.CarAdvert
import dev.harmeetsingh.caradverts.entity.CarAdvert._
import dev.harmeetsingh.caradverts.model.EnumProtocols.ordering
import dev.harmeetsingh.caradverts.repository.CarAdvertRepo
import dev.harmeetsingh.caradverts.repository.impl.CarAdvertRepoImpl._
import dev.harmeetsingh.caradverts.repository.impl.DynamoUtils._
import scala.concurrent.{ExecutionContextExecutor, Future}

object CarAdvertRepoImpl{
    private final val datePattern = DateTimeFormatter.ISO_LOCAL_DATE
    
    implicit val localDataFormat = DynamoFormat.coercedXmap[LocalDate, String, IllegalArgumentException](
        LocalDate.parse(_, datePattern)
    )(
        _.toString
    )
    
    final val CarTable: Table[CarAdvert] = Table[CarAdvert](TableName)
}
class CarAdvertRepoImpl (dynamoDBClient: DynamoDBClient)(implicit system: ActorSystem) extends CarAdvertRepo {
    
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
    
    def findAllCarAdverts(sortingField : Option[String]) : Future[List[CarAdvert]] = {
    
        val data = futureOfIterableEitherToFutureSeq (ScanamoAlpakka.exec(dbClient)(CarTable.scan())).map(_.toList)
    
        implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
    
        sortingField match{
            case Some("title") => data.map(_.sortBy(_.title))
            case Some("fuel") => data.map(_.sortBy(_.fuel))
            case Some("price") => data.map(_.sortBy(_.price))
            case Some("new") => data.map(_.sortBy(_.`new`))
            case Some("mileage") => data.map(_.sortBy(_.mileage))
            case Some("firstRegistration") => data.map(_.sortBy(_.firstRegistration))
            case _ => data.map(_.sortBy(_.id))
        }
    }
    
    def findCarAdvertByID(id: Int): Future[Option[CarAdvert]] = futureOfOptionEitherToFutureOption{
        ScanamoAlpakka.exec(dbClient)(CarTable.get(ID -> id))
    }
    
    def addOrUpdateCarAdvert(carAdvert: CarAdvert): Future[Option[CarAdvert]] = futureOfOptionEitherToFutureOption {
        ScanamoAlpakka.exec(dbClient)(CarTable.put(carAdvert))
    }
    
    def deleteCarAdvertByID(id: Int) : Future[Done] = {
        ScanamoAlpakka.exec(dbClient)(CarTable.delete(ID -> id)).map(deleteItemResult => Done)
    }
}
