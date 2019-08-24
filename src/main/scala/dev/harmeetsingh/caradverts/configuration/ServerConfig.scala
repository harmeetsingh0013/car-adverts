package dev.harmeetsingh.caradverts.configuration

import com.softwaremill.macwire.wire

trait ServerConfig {
    
    self : AkkaConfig =>
    
    val server = wire[Server]
}
