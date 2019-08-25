package dev.harmeetsingh.caradverts.service

import akka.Done
import dev.harmeetsingh.caradverts.entity.CarAdvert
import dev.harmeetsingh.caradverts.utils.Errors
import scala.concurrent.Future

trait CarAdvertService {
    def getAllCarAdverts(sort: Option[String]): Future[List[CarAdvert]]
    
    def findCarAdvertByID(id: Int): Future[Option[CarAdvert]]
    
    def insertOrUpdateCarAdvert(carAdvert: CarAdvert): Future[Either[Errors, Done]]
    
    def deleteCarAdvertByID(id: Int): Future[Done]
}
