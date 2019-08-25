package dev.harmeetsingh.caradverts.controller

import akka.http.scaladsl.server.Route
import dev.harmeetsingh.caradverts.service.CarAdvertService
import scala.concurrent.ExecutionContext
import dev.harmeetsingh.caradverts.model.CarAdvertProtocol._
import dev.harmeetsingh.caradverts.model.{BasicResponse, InsertOrUpdateCarAdvertRequest}

class CarAdvertRoute (carService: CarAdvertService)(implicit ec : ExecutionContext) extends BaseRoute {
    
    val route: Route = {
        pathPrefix("car-adverts") {
            getCarAdvertById ~
            getAllCarAdverts ~
            insertNewCarAdvert ~
            updateCarAdvert ~
            deleteCarAdvert
        }
    }
    
    private def getCarAdvertById: Route = get {
        path(IntNumber) { id =>
            complete(carService.findCarAdvertByID(id))
        }
    }
    
    private def getAllCarAdverts: Route = get {
        parameter('sort.?){ sort =>
            complete(carService.getAllCarAdverts(sort))
        }
    }
    
    private def deleteCarAdvert: Route = delete {
        path(IntNumber){ id =>
            complete{
                carService.deleteCarAdvertByID(id).map(_ => BasicResponse(status = true))
            }
        }
    }
    
    private def insertNewCarAdvert: Route = post {
        handleCarAdvertRequest
    }
    
    private def updateCarAdvert: Route = put {
        handleCarAdvertRequest
    }
    
    private def handleCarAdvertRequest: Route = entity(as[InsertOrUpdateCarAdvertRequest]) { carAdvertRequest =>
        val carAdvert = carAdvertRequest.carAdvert
        onSuccess(carService.insertOrUpdateCarAdvert(carAdvert)) {
            case Left(error) => complete(BasicResponse(status = false, message = Some(error.message)))
            case Right(result) => complete(BasicResponse(status = true))
        }
    }
}
