package dev.harmeetsingh.caradverts.repository.impl

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.gu.scanamo.DynamoFormat
import dev.harmeetsingh.caradverts.configuration.dependencies.db.DynamoDBClient
import dev.harmeetsingh.caradverts.entity.Car
import dev.harmeetsingh.caradverts.model.Fuel
import dev.harmeetsingh.caradverts.repository.DynamoTestTrait
import org.scalatest.{BeforeAndAfterAll, Matchers}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class CarRepoImplSpec extends DynamoTestTrait with BeforeAndAfterAll with Matchers {
    private final val datePattern = DateTimeFormatter.ISO_LOCAL_DATE
    implicit val localDataFormat = DynamoFormat.coercedXmap[LocalDate, String, IllegalArgumentException](
        LocalDate.parse(_, datePattern)
    )(
        _.toString
    )
    
    implicit val timeouts : Timeout = Timeout(5 seconds)
    implicit val system : ActorSystem = ActorSystem()
    
    implicit val materializer : ActorMaterializer = ActorMaterializer()
    private val dbClient = new DynamoDBClient
    private val carRepo = new CarRepoImpl(dbClient)
    
    private final val car500 = Car(1, "Car500", Fuel.DIESEL, 500, false, Some(12), None)
    private final val car600 = Car(3, "Car600", Fuel.GASOLINE, 600, false, Some(23), None)
    private final val car700 = Car(2, "Car700", Fuel.DIESEL, 700, false, Some(3), None)
    
    override def afterAll() : Unit = {
        super.afterAll()
        Await.result(system.terminate(), 5.seconds)
    }
    
    it should "create table successfully" in {
        carRepo.createTable.map(_ should === (Done))
    }
    
    it should "return all car records sorted by id" in {
        for {
            _ <- carRepo.addNewCar(car500)
            _ <- carRepo.addNewCar(car600)
            _ <- carRepo.addNewCar(car700)
            cars <- carRepo.findAllCar(None)
        } yield (cars should contain theSameElementsInOrderAs  List(car500, car700, car600))
    }
    
    it should "return all car records sorted by title" in {
        carRepo.findAllCar(Some("title")).map {
            _   should contain theSameElementsInOrderAs  List(car500, car600, car700)
        }
    }
    
    it should "return all car records sorted by price" in {
        carRepo.findAllCar(Some("price")).map {
            _   should contain theSameElementsInOrderAs  List(car500, car600, car700)
        }
    }
    
    it should "return all car records sorted by mileage" in {
        carRepo.findAllCar(Some("mileage")).map {
            _   should contain theSameElementsInOrderAs  List(car700, car500, car600)
        }
    }
}
