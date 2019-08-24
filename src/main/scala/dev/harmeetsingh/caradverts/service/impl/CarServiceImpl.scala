package dev.harmeetsingh.caradverts.service.impl

import akka.Done
import dev.harmeetsingh.caradverts.entity.Car
import dev.harmeetsingh.caradverts.repository.CarRepo
import dev.harmeetsingh.caradverts.service.CarService
import dev.harmeetsingh.caradverts.utils.{Errors, MandatoryFieldValueMissing}
import scala.concurrent.{ExecutionContext, Future}

class CarServiceImpl (carRepo: CarRepo) (implicit ec : ExecutionContext)extends CarService {
    
    def getAllCarAdverts(sort: Option[String]): Future[List[Car]] = {
        carRepo.findAllCar(sort.map(_.trim.toLowerCase))
    }
    
    def findCarByID(id : Int): Future[Option[Car]] =
        carRepo.findCarByID(id)
    
    def insertCarAdvert(car: Car): Future[Either[Errors, Done]] = {
        if(!car.`new` && (!car.mileage.isDefined || !car.firstRegistration.isDefined) ) {
            Future.successful(Left(MandatoryFieldValueMissing("For old cars mileage and firstRegistration values requires")))
        }else {
            carRepo.addNewCar(car).map(_=> Right(Done))
        }
    }
}
