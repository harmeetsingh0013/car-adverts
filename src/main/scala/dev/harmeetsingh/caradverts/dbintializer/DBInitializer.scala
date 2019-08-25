package dev.harmeetsingh.caradverts.dbintializer

import akka.Done
import akka.stream.alpakka.dynamodb.scaladsl.DynamoImplicits._
import com.amazonaws.services.dynamodbv2.model.{DescribeTableRequest, TableStatus}
import dev.harmeetsingh.caradverts.configuration.dependencies.db.DynamoDBClient
import dev.harmeetsingh.caradverts.entity.CarAdvert
import dev.harmeetsingh.caradverts.repository.CarAdvertRepo
import scala.concurrent.{ExecutionContext, Future}

class DBInitializer(dynamoDBClient: DynamoDBClient,  carRepo: CarAdvertRepo)(implicit executionContext : ExecutionContext) {
    
    def initDB: Future[Done] = {
        dynamoDBClient.dbClient
            .single(new DescribeTableRequest(CarAdvert.TableName))
            .flatMap{ describeTableStatus =>
                TableStatus.fromValue(describeTableStatus.getTable.getTableStatus) match {
                    case TableStatus.ACTIVE =>
                        Future.successful(Done)
                    case _ =>
                        carRepo.createTable
                }
            }
    }
}
