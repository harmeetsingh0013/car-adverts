package dev.harmeetsingh.caradverts.service.impl

import java.time.LocalDate
import akka.Done
import dev.harmeetsingh.caradverts.entity.CarAdvert
import dev.harmeetsingh.caradverts.model.Fuel
import dev.harmeetsingh.caradverts.repository.CarAdvertRepo
import dev.harmeetsingh.caradverts.utils.MandatoryFieldValueMissing
import org.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, EitherValues, Matchers}
import scala.concurrent.Future

class CarAdvertServiceImplSpec extends AsyncWordSpec with EitherValues with Matchers with MockitoSugar{
    private val carAdvertRepo = mock[CarAdvertRepo]
    
    val carAdvertServiceImpl = new CarAdvertServiceImpl(carAdvertRepo)
    
    private final val car500 = CarAdvert(1, "Car500", Fuel.DIESEL, 500, true, Some(12), None)
    private final val car600 = CarAdvert(3, "Car600", Fuel.GASOLINE, 600, true, Some(23), None)
    private final val car700 = CarAdvert(2, "Car700", Fuel.DIESEL, 700, true, Some(3), None)
    
    "CarAdvertService" should {
        
        "insert car advert for new car successfully" in {
            when(carAdvertRepo.addOrUpdateCarAdvert(car500)).thenReturn(Future.successful(None))
            carAdvertServiceImpl.insertOrUpdateCarAdvert(car500).map {
                _.right.value should === (Done)
            }
        }
        
        "insert car advert for existing car" in {
            val updateCar500 = car500.copy(`new`= false, firstRegistration = Some(LocalDate.now()))
            when(carAdvertRepo.addOrUpdateCarAdvert(updateCar500)).thenReturn(Future.successful(None))
    
            carAdvertServiceImpl.insertOrUpdateCarAdvert(updateCar500).map {
                _.right.value should === (Done)
            }
        }
        
        "unable to insert car advert because firstRegistration missing in old car" in {
            val updateCar500 = car500.copy(`new`= false)
    
            carAdvertServiceImpl.insertOrUpdateCarAdvert(updateCar500).map {
                _.left.value should === (MandatoryFieldValueMissing("For old cars mileage and firstRegistration values requires"))
            }
        }
    
        "unable to insert car advert because mileage missing in old car" in {
            val updateCar500 = car500.copy(`new`= false, firstRegistration = Some(LocalDate.now()), mileage = None)
        
            carAdvertServiceImpl.insertOrUpdateCarAdvert(updateCar500).map {
                _.left.value should === (MandatoryFieldValueMissing("For old cars mileage and firstRegistration values requires"))
            }
        }
    
        "unable to insert car advert because mileage and firstRegistration missing in old car" in {
            val updateCar500 = car500.copy(`new`= false, mileage = None)
        
            carAdvertServiceImpl.insertOrUpdateCarAdvert(updateCar500).map {
                _.left.value should === (MandatoryFieldValueMissing("For old cars mileage and firstRegistration values requires"))
            }
        }
        
        "get all car adverts order by id" in {
            when(carAdvertRepo.findAllCarAdverts(None)).thenReturn(Future.successful(List(car500, car700, car600)))
            
            carAdvertServiceImpl.getAllCarAdverts(None).map {
                _ should contain theSameElementsInOrderAs  List(car500, car700, car600)
            }
        }
        
        "find car advert by id successfully" in {
            when(carAdvertRepo.findCarAdvertByID(car500.id)).thenReturn(Future.successful(Some(car500)))
            
            carAdvertServiceImpl.findCarAdvertByID(car500.id).map {
                _ should === (Some(car500))
            }
        }
    
        "find car advert by with failure" in {
            when(carAdvertRepo.findCarAdvertByID(car600.id)).thenReturn(Future.successful(None))
        
            carAdvertServiceImpl.findCarAdvertByID(car600.id).map {
                _ should === (None)
            }
        }
        
        "delete car advert by ID" in {
            when(carAdvertRepo.deleteCarAdvertByID(car700.id)).thenReturn(Future.successful(Done))
            
            carAdvertServiceImpl.deleteCarAdvertByID(car700.id).map {
                _ should === (Done)
            }
        }
    }
}
