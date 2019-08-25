package dev.harmeetsingh.caradverts.repository.impl

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.gu.scanamo.DynamoFormat
import dev.harmeetsingh.caradverts.configuration.dependencies.db.DynamoDBClient
import dev.harmeetsingh.caradverts.entity.CarAdvert
import dev.harmeetsingh.caradverts.model.Fuel
import dev.harmeetsingh.caradverts.repository.DynamoTestTrait
import org.scalatest.{BeforeAndAfterAll, Matchers}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class CarAdvertAdvertRepoImplSpec extends DynamoTestTrait with BeforeAndAfterAll with Matchers {
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
    private val carRepo = new CarAdvertRepoImpl(dbClient)
    
    private final val car500 = CarAdvert(1, "Car500", Fuel.DIESEL, 500, false, Some(12), Some(LocalDate.now().plusDays(1)))
    private final val car600 = CarAdvert(3, "Car600", Fuel.GASOLINE, 600, false, Some(23), Some(LocalDate.now().plusDays(2)))
    private final val car700 = CarAdvert(2, "Car700", Fuel.DIESEL, 700, false, Some(3), Some(LocalDate.now().plusDays(3)))
    
    override def afterAll() : Unit = {
        super.afterAll()
        Await.result(system.terminate(), 5.seconds)
    }
    
    it should "create table successfully" in {
        carRepo.createTable.map(_ should === (Done))
    }
    
    it should "return all car-advert records sorted by id" in {
        for {
            _ <- carRepo.addOrUpdateCarAdvert(car500)
            _ <- carRepo.addOrUpdateCarAdvert(car600)
            _ <- carRepo.addOrUpdateCarAdvert(car700)
            cars <- carRepo.findAllCarAdverts(None)
        } yield (cars should contain theSameElementsInOrderAs  List(car500, car700, car600))
    }
    
    it should "return all car-advert records sorted by title" in {
        carRepo.findAllCarAdverts(Some("title")).map {
            _   should contain theSameElementsInOrderAs  List(car500, car600, car700)
        }
    }
    
    it should "return all car-advert records sorted by price" in {
        carRepo.findAllCarAdverts(Some("price")).map {
            _   should contain theSameElementsInOrderAs  List(car500, car600, car700)
        }
    }
    
    it should "return all car-advert records sorted by mileage" in {
        carRepo.findAllCarAdverts(Some("mileage")).map {
            _   should contain theSameElementsInOrderAs  List(car700, car500, car600)
        }
    }
    
    it should "return all car-advert records sorted by firstRegistration" in {
        carRepo.findAllCarAdverts(Some("firstRegistration")).map {
            _   should contain theSameElementsInOrderAs  List(car500, car600, car700)
        }
    }
    
    it should "find car advert by ID with success" in {
        carRepo.findCarAdvertByID(car500.id).map {
            _ should === (Some(car500))
        }
    }
    
    it should "find car advert by ID with failure" in {
        carRepo.findCarAdvertByID(13).map {
            _ should === (None)
        }
    }
    
    it should "update car advert with success" in {
        carRepo.addOrUpdateCarAdvert(car500.copy(title = "Car5000")).flatMap { _ =>
            carRepo.findCarAdvertByID(car500.id).map {
                _ should === (Some(car500.copy(title = "Car5000")))
            }
        }
    }
    
    it should "delete car advert by id with success" in {
        carRepo.deleteCarAdvertByID(car500.id).flatMap { _ =>
            carRepo.findAllCarAdverts(None).map {
                _ should contain theSameElementsInOrderAs  List(car700, car600)
            }
        }
    }
}
