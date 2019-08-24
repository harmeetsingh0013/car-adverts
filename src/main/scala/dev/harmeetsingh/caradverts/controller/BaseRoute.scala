package dev.harmeetsingh.caradverts.controller

import akka.http.scaladsl.server.{Directives, Route}

trait BaseRoute extends Directives {
    
    def route: Route
}
