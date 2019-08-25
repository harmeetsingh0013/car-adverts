package dev.harmeetsingh.caradverts.service.impl

import akka.Done
import dev.harmeetsingh.caradverts.entity.CarAdvert
import dev.harmeetsingh.caradverts.repository.CarAdvertRepo
import dev.harmeetsingh.caradverts.service.CarAdvertService
import dev.harmeetsingh.caradverts.utils.{Errors, MandatoryFieldValueMissing}
import scala.concurrent.{ExecutionContext, Future}

class CarAdvertServiceImpl (carRepo: CarAdvertRepo) (implicit ec : ExecutionContext)extends CarAdvertService {
    
    def getAllCarAdverts(sort: Option[String]): Future[List[CarAdvert]] = {
        carRepo.findAllCarAdverts(sort.map(_.trim.toLowerCase))
    }
    
    def findCarAdvertByID(id : Int): Future[Option[CarAdvert]] =
        carRepo.findCarAdvertByID(id)
    
    def insertOrUpdateCarAdvert(carAdvert: CarAdvert): Future[Either[Errors, Done]] = {
        if(!carAdvert.`new` && (!carAdvert.mileage.isDefined || !carAdvert.firstRegistration.isDefined) ) {
            Future.successful(Left(MandatoryFieldValueMissing("For old cars mileage and firstRegistration values requires")))
        }else {
            carRepo.addOrUpdateCarAdvert(carAdvert).map(_=> Right(Done))
        }
    }
    
    def deleteCarAdvertByID(id: Int): Future[Done] = {
        carRepo.deleteCarAdvertByID(id)
    }
}
