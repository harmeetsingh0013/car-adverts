package dev.harmeetsingh.caradverts.configuration.dependencies

import com.softwaremill.macwire.wire
import dev.harmeetsingh.caradverts.configuration.AkkaConfig
import dev.harmeetsingh.caradverts.service.impl.CarAdvertServiceImpl

trait ServiceConfig extends RepositoryConfig {
    
    self : AkkaConfig =>
    
    val carAdvertService = wire[CarAdvertServiceImpl]
}
