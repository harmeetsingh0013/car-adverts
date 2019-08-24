package dev.harmeetsingh.caradverts.configuration

import com.typesafe.config.ConfigFactory

object ConfigHelper {
    private val config = ConfigFactory.load()
    
    final val APP_HOST = config.getString("app.server.host")
    final val APP_PORT = config.getInt("app.server.port")
}
