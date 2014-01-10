package models.domain.services.impl

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import models.data.dao.RouteDaoComponent
import models.domain.services.GymServiceComponent
import models.JugjaneException
import scala.util.{Success, Failure}
import scala.concurrent.{Await, Promise}
import models.data.{ model => dat }
import reactivemongo.bson.BSONObjectID
import test.TestUtils
import models.domain.gym.Demo

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
      val route = dat.Route(Some(BSONObjectID.generate),
        "demo",
        "/home/rado/x.jpeg",
        "demo1",
        "white_black",
        "blaaa",
        "Bouldering",
        true,
        List("volumes", "corner"),
        Map("boring" -> 3, "whathow" -> 0))
      routeDao.getByRouteId("123").returns(Promise.successful(route).future)
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
      val route = dat.Route(None,
        "demo",
        "/home/rado/x.jpeg",
        "demo1",
        "white_black",
        "blaaa",
        "Bouldering",
        true,
        List("volumes", "corner"),
        Map("boring" -> 3, "whathow" -> 0))
      routeDao.getByRouteId("123").returns(Promise.successful(route).future)

      // Execute
      routeService.getByRouteId("123") must throwA[IllegalArgumentException].await

      // Verify
      there was one(routeDao).getByRouteId("123")
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
