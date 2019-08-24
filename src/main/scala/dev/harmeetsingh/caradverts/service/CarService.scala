package dev.harmeetsingh.caradverts.service

import akka.Done
import dev.harmeetsingh.caradverts.entity.Car
import dev.harmeetsingh.caradverts.utils.Errors
import scala.concurrent.Future

trait CarService {
    def getAllCarAdverts(sort: Option[String]): Future[List[Car]]
    
    def findCarByID(id: Int): Future[Option[Car]]
    
    def insertCarAdvert(car: Car): Future[Either[Errors, Done]]
}
