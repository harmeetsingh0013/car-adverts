package dev.harmeetsingh.caradverts.repository.impl

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException
import com.gu.scanamo.error.{ConditionNotMet, DynamoReadError, MissingProperty, ScanamoError}
import dev.harmeetsingh.caradverts.utils.{DBConditionalCheckFailedException, DatabaseException}
import scala.concurrent.{ExecutionContext, Future}

object DynamoUtils {
    type FutureOfOptionOfEither[T] = Future[Option[Either[DynamoReadError, T]]]
    type FutureOfIterableOfEither[T] = Future[Iterable[Either[DynamoReadError, T]]]
    type FutureOfEither[T] = Future[Either[ScanamoError, T]]
    
    def futureOfOptionEitherToFutureOption[T](future : FutureOfOptionOfEither[T])
        (implicit executionContext : ExecutionContext) : Future[Option[T]] = {
        future.flatMap {
            case Some(either) => either match {
                case Right(value) => Future.successful(Some(value))
                case Left(error) => Future.failed(DatabaseException(error.toString))
            }
            case None => Future.successful(None)
        }
    }
    
    def futureOfIterableEitherToFutureSeq[T](future : FutureOfIterableOfEither[T])
        (implicit executionContext : ExecutionContext) : Future[Iterable[T]] = {
        future.flatMap {
            seq : Iterable[Either[DynamoReadError, T]] =>
                val errors = seq.filter(_.isLeft)
                    .map(_.left.getOrElse(MissingProperty).toString)
                    .fold("")(_ + _)
                
                if(errors.isEmpty) {
                    Future.successful(seq.flatMap(_.right.toOption))
                }
                else {
                    Future.failed(DatabaseException(errors))
                }
        }
    }
    
    def futureOfEitherToFuture[T](future : FutureOfEither[T])(implicit executionContext : ExecutionContext) : Future[T] = {
        future.flatMap {
            case Right(value) => Future.successful(value)
            
            case Left(ConditionNotMet(_ : ConditionalCheckFailedException)) => {
                Future.failed(DBConditionalCheckFailedException())
            }
            
            case Left(error) => {
                Future.failed(DatabaseException(error.toString))
            }
        }
    }
}
