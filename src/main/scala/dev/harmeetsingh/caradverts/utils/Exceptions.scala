package dev.harmeetsingh.caradverts.utils

final case class DatabaseException(message : String, exception : Throwable = new Exception) extends Exception(message, exception){
    override def toString : String = s"${getClass.getName}($message,$exception)"
}

final case class DBConditionalCheckFailedException(message : String = "DB conditional check failed") extends Exception(message)
