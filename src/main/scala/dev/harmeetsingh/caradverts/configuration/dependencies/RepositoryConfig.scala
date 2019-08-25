package dev.harmeetsingh.caradverts.configuration.dependencies

import dev.harmeetsingh.caradverts.configuration.AkkaConfig
import dev.harmeetsingh.caradverts.configuration.dependencies.db.DynamoDBClient
import com.softwaremill.macwire.wire
import dev.harmeetsingh.caradverts.repository.impl.CarAdvertRepoImpl

trait RepositoryConfig {
    self : AkkaConfig =>
    
    val dynamoDBClient = wire[DynamoDBClient]
    val careRepo = wire[CarAdvertRepoImpl]
}
