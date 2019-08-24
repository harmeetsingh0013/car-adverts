package dev.harmeetsingh.caradverts.configuration

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext

trait AkkaConfig {
    implicit lazy val m_system : ActorSystem = ActorSystem("CarAdverts")
    
    implicit lazy val m_dispatcher : ExecutionContext = m_system.dispatcher
    
    implicit lazy val materializer: ActorMaterializer = ActorMaterializer()
}
