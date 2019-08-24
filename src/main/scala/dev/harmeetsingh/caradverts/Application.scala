package dev.harmeetsingh.caradverts

import dev.harmeetsingh.caradverts.configuration.ControllerConfig
import dev.harmeetsingh.caradverts.dbintializer.DBInitializer

object Application extends App with ControllerConfig{
    
    val message =
        """
          |   _____                         _                _
          |  / ____|               /\      | |              | |
          | | |     __ _ _ __     /  \   __| |_   _____ _ __| |_ ___
          | | |    / _` | '__|   / /\ \ / _` \ \ / / _ \ '__| __/ __|
          | | |___| (_| | |     / ____ \ (_| |\ V /  __/ |  | |_\__ \
          |  \_____\__,_|_|    /_/    \_\__,_| \_/ \___|_|   \__|___/
          |
        """.stripMargin
    
    val dbInitializer = new DBInitializer(dynamoDBClient, careRepo)
    dbInitializer.initDB map { _ =>
        server.bind(routes = routes)
    
        println(message)
    }
}
