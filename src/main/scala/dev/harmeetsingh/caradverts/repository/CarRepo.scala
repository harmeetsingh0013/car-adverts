package dev.harmeetsingh.caradverts.repository

import akka.Done
import dev.harmeetsingh.caradverts.entity.Car
import scala.concurrent.Future

trait CarRepo {
    def createTable : Future[Done]
    
    def findAllCar(sort : Option[String]): Future[List[Car]]
    
    def findCarByID(id: Int): Future[Option[Car]]
    
    def addNewCar(car: Car): Future[Option[Car]]
}
