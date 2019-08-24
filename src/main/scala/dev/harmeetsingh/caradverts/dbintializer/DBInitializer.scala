package dev.harmeetsingh.caradverts.dbintializer

import akka.Done
import akka.stream.alpakka.dynamodb.scaladsl.DynamoImplicits._
import com.amazonaws.services.dynamodbv2.model.{DescribeTableRequest, TableStatus}
import dev.harmeetsingh.caradverts.configuration.dependencies.db.DynamoDBClient
import dev.harmeetsingh.caradverts.entity.Car
import dev.harmeetsingh.caradverts.repository.CarRepo
import scala.concurrent.{ExecutionContext, Future}

class DBInitializer(dynamoDBClient: DynamoDBClient,  carRepo: CarRepo)(implicit executionContext : ExecutionContext) {
    
    def initDB: Future[Done] = {
        dynamoDBClient.dbClient
            .single(new DescribeTableRequest(Car.TableName))
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
