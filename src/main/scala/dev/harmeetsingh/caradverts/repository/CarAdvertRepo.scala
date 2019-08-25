package dev.harmeetsingh.caradverts.repository

import akka.Done
import dev.harmeetsingh.caradverts.entity.CarAdvert
import scala.concurrent.Future

trait CarAdvertRepo {
    def createTable : Future[Done]
    
    def findAllCarAdverts(sort : Option[String]): Future[List[CarAdvert]]
    
    def findCarAdvertByID(id: Int): Future[Option[CarAdvert]]
    
    def addOrUpdateCarAdvert(carAdvert: CarAdvert): Future[Option[CarAdvert]]
    
    def deleteCarAdvertByID(id: Int): Future[Done]
}
