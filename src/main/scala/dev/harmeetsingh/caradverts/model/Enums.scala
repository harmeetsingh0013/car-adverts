package dev.harmeetsingh.caradverts.model

import spray.json.{JsString, JsValue, RootJsonFormat, deserializationError}

sealed trait Fuel
case object Fuel{
    case object GASOLINE extends Fuel
    case object DIESEL extends Fuel
    
    def withName(string: String): Fuel = string.toUpperCase match {
        case "GASOLINE" => Fuel.GASOLINE
        case "DIESEL" => Fuel.DIESEL
    }
}

object EnumProtocols {
    implicit object FuelFormatter extends  RootJsonFormat[Fuel] {
        override def read(json : JsValue) = json match {
            case JsString(fuel) if fuel.trim.toUpperCase == "GASOLINE" => Fuel.GASOLINE
            case JsString(fuel) if fuel.trim.toUpperCase == "DIESEL" => Fuel.DIESEL
            case _ => deserializationError("Invalid fuel type")
        }
    
        override def write(obj : Fuel) = JsString(obj.toString)
    }
    
    implicit val ordering = new Ordering[Fuel] {
        override def compare(x : Fuel, y : Fuel) = x.toString.compareTo(y.toString)
    }
}