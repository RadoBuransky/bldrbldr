package controllers

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import models.domain.services.{PhotoServiceComponent, RouteServiceComponent, AuthServiceComponent, GymServiceComponent}
import scala.concurrent.Promise
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models.JugjaneException
import scala.util.{Success, Failure}
import models.domain.gym.Demo
import play.api.http.Status._
import scala.util.Success
import models.JugjaneException
import scala.util.Failure
import scala.util.Success
import models.JugjaneException
import scala.util.Failure
import com.jugjane.test.TestData

class RouteControllerSpec extends Specification with Mockito {
  "flag" should {
    "delegate to service" in new RouteControllerScope {
      // Setup
      routeService.incFlag("123", "aaa") returns Promise.successful().future

      // Execute
      val result = flag("demo", "123", "aaa")(FakeRequest(PUT, "/climbing/demo/123/flag/aaa"))

      // Assert
      status(result) must equalTo(OK)

      // Verfy
      there was one(routeService).incFlag("123", "aaa")
    }
  }

  "delete" should {
    "fail for nonexisting gym" in new RouteControllerScope {
      // Setup
      gymService.get("demo").returns(Failure(new JugjaneException("x")))

      // Execute
      val result = delete("demo", "123")(FakeRequest(DELETE, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(INTERNAL_SERVER_ERROR)

      // Verfy
      there was one(gymService).get("demo")
    }

    "fail for failed authorization" in new RouteControllerScope {
      // Setup
      gymService.get("demo").returns(Success(Demo))
      authService.isAdmin(any, any).returns(Failure(new JugjaneException("x")))

      // Execute
      val result = delete("demo", "123")(FakeRequest(DELETE, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(INTERNAL_SERVER_ERROR)

      // Verfy
      there was one(gymService).get("demo")
      there was one(authService).isAdmin(any, any)
    }

    "fail for unauthorized access" in new RouteControllerScope {
      // Setup
      gymService.get("demo").returns(Success(Demo))
      authService.isAdmin(any, any).returns(Success(false))

      // Execute
      val result = delete("demo", "123")(FakeRequest(DELETE, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(UNAUTHORIZED)

      // Verfy
      there was one(gymService).get("demo")
      there was one(authService).isAdmin(any, any)
    }

    "fail for nonexisting route" in new RouteControllerScope {
      // Setup
      gymService.get("demo").returns(Success(Demo))
      authService.isAdmin(any, any).returns(Success(true))
      routeService.getByRouteId("123").returns(Promise.failed(new JugjaneException("x")).future)

      // Execute
      val result = delete("demo", "123")(FakeRequest(DELETE, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(NOT_FOUND)

      // Verfy
      there was one(gymService).get("demo")
      there was one(authService).isAdmin(any, any)
      there was one(routeService).getByRouteId("123")
    }

    "delete from S3 and DB"  in new RouteControllerScope {
      // Setup
      gymService.get("demo").returns(Success(Demo))
      authService.isAdmin(any, any).returns(Success(true))
      routeService.getByRouteId("123").returns(Promise.successful(TestData.route1).future)
      photoService.remove("/home/rado/123.jpeg").returns(Promise.successful().future)
      routeService.delete("123").returns(Promise.successful().future)

      // Execute
      val result = delete("demo", "123")(FakeRequest(DELETE, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(OK)

      // Verfy
      there was one(gymService).get("demo")
      there was one(authService).isAdmin(any, any)
      there was one(routeService).getByRouteId("123")
      there was one(photoService).remove("/home/rado/123.jpeg")
      there was one(routeService).delete("123")
    }
  }

  trait RouteControllerScope extends Scope with RouteController with RouteServiceComponent
    with GymServiceComponent with AuthServiceComponent with PhotoServiceComponent {
    val routeService: RouteService = mock[RouteService]
    val gymService: GymService = mock[GymService]
    val authService: AuthService = mock[AuthService]
    val photoService: PhotoService = mock[PhotoService]
  }

}
