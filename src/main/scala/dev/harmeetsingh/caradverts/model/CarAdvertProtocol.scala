package dev.harmeetsingh.caradverts.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import dev.harmeetsingh.caradverts.entity.Car
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, deserializationError}
import scala.util.Try
case class InsertCarAdvertRequest(carAdvert: Car)

case class BasicResponse(status: Boolean, message: Option[String] = None)

object CarAdvertProtocol extends SprayJsonSupport with DefaultJsonProtocol {
    
    private final val datePattern = DateTimeFormatter.ISO_LOCAL_DATE
    private final val dateParsingErrorMessage = "Invalid date format"
    
    implicit val dateFormatter = new RootJsonFormat[LocalDate] {
        override def read(json : JsValue) = json match {
            case JsString(date) => Try(LocalDate.parse(date, datePattern))
                .getOrElse(deserializationError(dateParsingErrorMessage))
            case _ => deserializationError(dateParsingErrorMessage)
        }
    
        override def write(obj : LocalDate) = JsString(datePattern.format(obj))
    }
    
    implicit val InsertCarAdvertRequestFormatter = jsonFormat1(InsertCarAdvertRequest)
    implicit val BasicResponseFormatter = jsonFormat2(BasicResponse)
}
