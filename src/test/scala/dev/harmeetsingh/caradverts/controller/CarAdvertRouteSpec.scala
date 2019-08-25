package dev.harmeetsingh.caradverts.controller

import java.time.LocalDate
import akka.Done
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import dev.harmeetsingh.caradverts.entity.CarAdvert
import dev.harmeetsingh.caradverts.model.CarAdvertProtocol._
import dev.harmeetsingh.caradverts.model.{BasicResponse, Fuel}
import dev.harmeetsingh.caradverts.service.CarAdvertService
import dev.harmeetsingh.caradverts.utils.MandatoryFieldValueMissing
import org.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.Future

class CarAdvertRouteSpec extends WordSpec with MockitoSugar with Matchers with ScalatestRouteTest {
    
    private val carAdvertService = mock[CarAdvertService]
    
    final val carAdvertRoute = new CarAdvertRoute(carAdvertService)
    
    private final val car500 = CarAdvert(1, "Car500", Fuel.DIESEL, 500, true, Some(12), Some(LocalDate.now()))
    private final val car600 = CarAdvert(3, "Car600", Fuel.GASOLINE, 600, true, Some(23), Some(LocalDate.now().plusDays(1)))
    private final val car700 = CarAdvert(2, "Car700", Fuel.DIESEL, 700, true, Some(3), Some(LocalDate.now().plusDays(2)))
    
    "CarAdvertRoute" should {
        "find car advert by id with success" in {
            val findCarAdvertByIDRequest = HttpRequest(
                HttpMethods.GET,
                uri = "/car-adverts/1")
        
            when(carAdvertService.findCarAdvertByID(1)).thenReturn(Future.successful(Some(car500)))
        
            findCarAdvertByIDRequest ~> carAdvertRoute.route ~> check {
                status should === (StatusCodes.OK)
                responseAs[CarAdvert] should === (car500)
            }
        }
    
        "find car advert by id with with empty body" in {
            val findCarAdvertByIDRequest = HttpRequest(
                HttpMethods.GET,
                uri = "/car-adverts/1")
        
            when(carAdvertService.findCarAdvertByID(1)).thenReturn(Future.successful(None))
        
            findCarAdvertByIDRequest ~> carAdvertRoute.route ~> check {
                status should === (StatusCodes.OK)
            }
        }
    
        "find all car adverts sort by id" in {
            val findAllCarAdverts = HttpRequest(
                HttpMethods.GET,
                uri = "/car-adverts")
        
            when(carAdvertService.getAllCarAdverts(None)).thenReturn(Future.successful(List(car500, car700, car600)))
        
            findAllCarAdverts ~> carAdvertRoute.route ~> check {
                status should === (StatusCodes.OK)
                responseAs[List[CarAdvert]] should contain theSameElementsInOrderAs(List(car500, car700, car600))
            }
        }
    
        "find all car adverts sort by title" in {
            val findAllCarAdverts = HttpRequest(
                HttpMethods.GET,
                uri = "/car-adverts?sort=title")
        
            when(carAdvertService.getAllCarAdverts(Some("title"))).thenReturn(Future.successful(List(car500, car600, car700)))
        
            findAllCarAdverts ~> carAdvertRoute.route ~> check {
                status should === (StatusCodes.OK)
                responseAs[List[CarAdvert]] should contain theSameElementsInOrderAs(List(car500, car600, car700))
            }
        }
    
        "delete car adverts by id" in {
            val deleteCarAdvertByIDRequest = HttpRequest(
                HttpMethods.DELETE,
                uri = "/car-adverts/1")
        
            when(carAdvertService.deleteCarAdvertByID(car500.id)).thenReturn(Future.successful(Done))
        
            deleteCarAdvertByIDRequest ~> carAdvertRoute.route ~> check {
                status should === (StatusCodes.OK)
                responseAs[BasicResponse] should === (BasicResponse(status = true))
            }
        }
        
        //TODO: Needs to be figuer out why this fails on all test cases
        "insert car advert for new car" in {
            
            val date = "2019-08-25"
            
            val requestBody =
                s"""{"carAdvert":{"id":1,"title":"Car500","fuel":"dieseL","price":500,"new":true,"mileage":12,"firstRegistration":"$date"}}"""
            
            val insertCarAdvertForNew = HttpRequest(
                HttpMethods.POST,
                uri = "/car-adverts",
                entity = HttpEntity(MediaTypes.`application/json`, requestBody))
    
            when(carAdvertService.insertOrUpdateCarAdvert(car500.copy(firstRegistration = Some(LocalDate.parse(date))))).thenReturn(Future.successful(Right(Done)))
            
            insertCarAdvertForNew ~> carAdvertRoute.route ~> check {
                status should === (StatusCodes.OK)
                responseAs[BasicResponse] should === (BasicResponse(status = true))
            }
        }
    
        "insert car advert for old car" in {
        
            val requestBody =
                s"""
                   |{
                   |	"carAdvert": {
                   |		"id": 1,
                   |		"title": "Car500",
                   |		"fuel": "DIESEL",
                   |		"price": 500,
                   |		"new": true,
                   |		"mileage": 12
                   |	}
                   |}
                """.stripMargin
        
            val insertCarAdvertForOld = HttpRequest(
                HttpMethods.POST,
                uri = "/car-adverts",
                entity = HttpEntity(MediaTypes.`application/json`, requestBody))
        
            when(carAdvertService.insertOrUpdateCarAdvert(car500.copy(firstRegistration = None)))
                .thenReturn(Future.successful(Left(MandatoryFieldValueMissing("For old cars mileage and firstRegistration values requires"))))
        
            insertCarAdvertForOld ~> carAdvertRoute.route ~> check {
                status should === (StatusCodes.OK)
                responseAs[BasicResponse] should === (BasicResponse(status = false, message = Some("For old cars mileage and firstRegistration values requires")))
            }
        }
        
        "update car advert" in {
            val date = "2019-08-25"
    
            val requestBody =
                s"""
                   |{
                   |	"carAdvert": {
                   |		"id": 1,
                   |		"title": "Car500",
                   |		"fuel": "DIESEL",
                   |		"price": 600,
                   |		"new": true,
                   |		"mileage": 12,
                   |		"firstRegistration" : "$date"
                   |	}
                   |}
                """.stripMargin
    
            val updateCarAdvert = HttpRequest(
                HttpMethods.PUT,
                uri = "/car-adverts",
                entity = HttpEntity(MediaTypes.`application/json`, requestBody))
    
            when(carAdvertService.insertOrUpdateCarAdvert(car500.copy(firstRegistration = Some(LocalDate.parse(date)), price = 600)))
                .thenReturn(Future.successful(Right(Done)))
    
            updateCarAdvert ~> carAdvertRoute.route ~> check {
                status should === (StatusCodes.OK)
                responseAs[BasicResponse] should === (BasicResponse(status = true))
            }
        }
    }
}
