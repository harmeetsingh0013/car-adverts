package dev.harmeetsingh.caradverts.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

sealed abstract class Errors(val message: String)

final case class MandatoryFieldValueMissing(override val message: String) extends Errors(message)

object ErrorProtocols extends DefaultJsonProtocol with SprayJsonSupport{
    implicit val MandatoryFieldValueMissingFormatter = jsonFormat1(MandatoryFieldValueMissing)
}
