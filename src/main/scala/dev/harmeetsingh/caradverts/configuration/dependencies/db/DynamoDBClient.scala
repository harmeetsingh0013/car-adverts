package dev.harmeetsingh.caradverts.configuration.dependencies.db

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.dynamodb.impl.DynamoSettings
import akka.stream.alpakka.dynamodb.scaladsl.DynamoClient

class DynamoDBClient(implicit system: ActorSystem, materializer: ActorMaterializer) {
    
    final val dbClient = DynamoClient(DynamoSettings(system))
}
