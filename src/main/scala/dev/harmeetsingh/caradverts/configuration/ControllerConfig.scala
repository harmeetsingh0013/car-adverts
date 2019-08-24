package dev.harmeetsingh.caradverts.configuration

import akka.http.scaladsl.server.{Directives, Route}
import dev.harmeetsingh.caradverts.configuration.dependencies.{RepositoryConfig, ServiceConfig}
import dev.harmeetsingh.caradverts.controller.{BaseRoute, CarRoute}
import com.softwaremill.macwire.wire
import akka.http.scaladsl.server.Directives._

trait ControllerConfig extends ServiceConfig with RepositoryConfig with ServerConfig with AkkaConfig {
    
    val seq: Seq[BaseRoute] = Seq (
        wire[CarRoute]
    )
    
    val routes: Route = seq.foldLeft[Route](Directives.reject) {
        case (acc, route) => acc ~ route.route
    }
}
