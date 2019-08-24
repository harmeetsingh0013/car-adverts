package dev.harmeetsingh.caradverts.controller

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Route
import dev.harmeetsingh.caradverts.service.CarService
import scala.concurrent.ExecutionContext
//import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import dev.harmeetsingh.caradverts.model.CarAdvertProtocol._
import dev.harmeetsingh.caradverts.model.{BasicResponse, InsertCarAdvertRequest}

class CarRoute (carService: CarService)(implicit ec : ExecutionContext) extends BaseRoute {
    
    val route: Route = {
        pathPrefix("car-advert") {
            getCarAdvertById ~
            getAllCarAdverts ~
            insertNewCarAdvert //~
//            updateCarAdvert ~
//            deleteCarAdvert
        }
    }
    
    private def getCarAdvertById: Route = get {
        path(IntNumber) { id =>
            complete(carService.findCarByID(id))
        }
    }
    
    private def getAllCarAdverts: Route = get {
        parameter('sort.?){ sort =>
            complete(carService.getAllCarAdverts(sort))
        }
    }
    
    private def insertNewCarAdvert: Route = post {
        entity(as[InsertCarAdvertRequest]) { carAdvertRequest =>
            val carAdvert = carAdvertRequest.carAdvert
            onSuccess(carService.insertCarAdvert(carAdvert)) {
                case Left(error) => complete(BasicResponse(status = false, message = Some(error.message)))
                case Right(result) => complete(BasicResponse(status = true))
            }
        }
    }
    
//    private def updateCarAdvert: Route = ???
//
//    private def deleteCarAdvert: Route = ???
}
