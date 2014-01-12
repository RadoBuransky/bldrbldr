package models.domain.services.impl

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import models.data.dao.RouteDaoComponent
import models.domain.services.GymServiceComponent
import models.JugjaneException
import scala.util.{Success, Failure}
import scala.concurrent.{Future, Await, Promise}
import models.data.{ model => dat }
import reactivemongo.bson.BSONObjectID
import test.TestUtils
import models.domain.gym.Demo
import com.jugjane.test.TestData

class RouteServiceImplSpec extends Specification with Mockito {
  "getByRouteId" should {
    "fail for nonexisting route" in new RouteServiceScope {
      // Setup
      routeDao.getByRouteId("123").returns(Promise.failed(new JugjaneException("x")).future)

      // Execute
      routeService.getByRouteId("123") must throwA[JugjaneException].await

      // Verify
      there was one(routeDao).getByRouteId("123")
    }

    "succeed for existing route" in new RouteServiceScope {
      // Setup
      routeDao.getByRouteId("123").returns(Promise.successful(TestData.datRoute1).future)
      gymService.get("demo").returns(Success(Demo))

      // Execute
      val result = TestUtils.await(routeService.getByRouteId("123"))

      // Assert
      result must beASuccessfulTry

      // Verify
      there was one(routeDao).getByRouteId("123")
      there was one(gymService).get("demo")
    }

    "fail if route has no _id" in new RouteServiceScope {
      // Setup
      val route = TestData.datRoute1.copy(_id = None)
      routeDao.getByRouteId("123").returns(Promise.successful(route).future)

      // Execute
      routeService.getByRouteId("123") must throwA[IllegalArgumentException].await

      // Verify
      there was one(routeDao).getByRouteId("123")
    }
  }

  "getByGymHandle" should {
    "delegate to DAO" in new RouteServiceScope {
      // Setup
      val datRoutes = TestData.datRoute1 :: TestData.datRoute1 :: Nil
      routeDao.findByGymhandle("demo").returns(Promise.successful(datRoutes).future)
      gymService.get("demo").returns(Success(Demo))

      // Execute
      val result = TestUtils.await(routeService.getByGymHandle("demo"))

      // Assert
      result must beASuccessfulTry
      result.get.size mustEqual 2

      // Verify
      there was one(routeDao).findByGymhandle("demo")
      there was two(gymService).get("demo")
    }
  }

  "delete" should {
    "delegate to dao" in new RouteServiceScope {
      // Setup
      routeDao.disable("666").returns(Promise.successful().future)

      // Execute & assert
      TestUtils.await(routeService.delete("666")) must beASuccessfulTry

      // Verify
      there was one(routeDao).disable("666")
    }
  }

  "incFlag" should {
    "delegate to dao" in new RouteServiceScope {
      // Setup
      routeDao.incFlag("1", "2").returns(Promise.successful().future)

      // Execute & assert
      TestUtils.await(routeService.incFlag("1", "2")) must beASuccessfulTry

      // Verify
      there was one(routeDao).incFlag("1", "2")
    }
  }

  trait RouteServiceScope extends Scope with RouteServiceComponentImpl with GymServiceComponent
    with RouteDaoComponent {
    val gymService: GymService = mock[GymService]
    val routeDao: RouteDao = mock[RouteDao]
  }
}
