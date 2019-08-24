package dev.harmeetsingh.caradverts.configuration

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

class Server(implicit val system: ActorSystem, materializer: ActorMaterializer) {
    
    def bind(routes: Route) =
        Http().bindAndHandle(Route.handlerFlow(routes), ConfigHelper.APP_HOST, ConfigHelper.APP_PORT)
}