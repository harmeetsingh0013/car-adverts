package dev.harmeetsingh.caradverts.entity

import java.time.LocalDate
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import dev.harmeetsingh.caradverts.model.Fuel
import spray.json.DefaultJsonProtocol
import dev.harmeetsingh.caradverts.model.CarAdvertProtocol._

final case class CarAdvert(
    id: Int,
    title: String,
    fuel : Fuel,
    price: Int,
    `new`: Boolean,
    mileage : Option[Int],
    firstRegistration: Option[LocalDate]
)

object CarAdvert extends SprayJsonSupport with DefaultJsonProtocol {
    
    final val ID : Symbol = 'id
    final val Title : Symbol = 'title
    final val Fuel : Symbol = 'fuel
    final val Price : Symbol = 'price
    final val `New` : Symbol = 'new
    final val Mileage : Symbol = 'mileage
    final val FirstRegistration : Symbol = 'firstRegistration
    
    final val TableName = "cars_adverts"
    import dev.harmeetsingh.caradverts.model.EnumProtocols._
    
    implicit val formatter  = jsonFormat7(CarAdvert.apply)
}
