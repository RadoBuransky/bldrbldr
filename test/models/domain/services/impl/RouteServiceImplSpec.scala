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
      routeDao.getByRouteId("123").returns(Promise.failed(new JugjaneException("x")).future)
      routeService.getByRouteId("123") must throwA[JugjaneException].await
    }

    "succeed for existing route" in new RouteServiceScope {
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

      val result = TestUtils.await(routeService.getByRouteId("123"))

      result must beASuccessfulTry
    }
  }

  trait RouteServiceScope extends Scope with RouteServiceComponentImpl with GymServiceComponent
    with RouteDaoComponent {
    val gymService: GymService = mock[GymService]
    val routeDao: RouteDao = mock[RouteDao]
  }
}
