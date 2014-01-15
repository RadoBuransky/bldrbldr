package com.jugjane.controllers

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import models.domain.services.{PhotoServiceComponent, RouteServiceComponent, AuthServiceComponent, GymServiceComponent}
import scala.concurrent.Promise
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models.domain.gym.Demo
import scala.util.Success
import models.JugjaneException
import scala.util.Failure
import com.jugjane.test.TestData
import java.net.URL
import play.api.mvc.{Request, MultipartFormData}
import play.api.mvc.MultipartFormData.{FilePart, BadPart, MissingFilePart}
import play.api.libs.Files.TemporaryFile

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
      routeService.getByRouteId("123").returns(Promise.successful(TestData.domRoute1).future)
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

  "get" should {
    "fail if route doesn't exist" in new RouteControllerScope {
      // Setup
      routeService.getByRouteId("123").returns(Promise.failed(new JugjaneException("x")).future)

      // Execute
      val result = get("demo", "123")(FakeRequest(GET, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(NOT_FOUND)

      // Verify
      there was one(routeService).getByRouteId("123")
    }

    "fail if route is disabled" in new RouteControllerScope {
      // Setup
      val route = TestData.domRoute1.copy(enabled = false)
      routeService.getByRouteId("123").returns(Promise.successful(route).future)

      // Execute
      val result = get("demo", "123")(FakeRequest(GET, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(NOT_FOUND)

      // Verify
      there was one(routeService).getByRouteId("123")
    }

    "fail if authorization service fails" in new RouteControllerScope {
      // Setup
      routeService.getByRouteId("123").returns(Promise.successful(TestData.domRoute1).future)
      authService.isAdmin(any, any).returns(Failure(new JugjaneException("x")))

      // Execute
      val result = get("demo", "123")(FakeRequest(GET, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(INTERNAL_SERVER_ERROR)

      // Verify
      there was one(routeService).getByRouteId("123")
      there was one(authService).isAdmin(any, any)
    }

    "return route" in new RouteControllerScope {
      // Setup
      routeService.getByRouteId("123").returns(Promise.successful(TestData.domRoute1).future)
      authService.isAdmin(any, any).returns(Success(true))
      photoService.getUrl(TestData.domRoute1.fileName).returns(new URL("http://xxx/"))

      // Execute
      val result = get("demo", "123")(FakeRequest(GET, "/climbing/demo/123"))

      // Assert
      status(result) must equalTo(OK)

      // Verify
      there was one(routeService).getByRouteId("123")
      there was one(authService).isAdmin(any, any)
      there was one(photoService).getUrl(TestData.domRoute1.fileName)
    }
  }

  "upload" should {
    "succeed if everything's ok" in new RouteControllerScope {
      // Data
      val dataParts = Map[String, Seq[String]]("grade" -> Seq("demo2"),
        "color" -> Seq("black"),
        "note" -> Seq("abc"),
        "categories" -> Seq("jugs,slopers,overhang"))
      val request= mock[Request[MultipartFormData[TemporaryFile]]]
      val tempFile = TemporaryFile("do_upload","spec")
      val part = FilePart("photo", "testPhoto.jpeg", Some("image/jpeg"), tempFile)
      val files = Seq[FilePart[TemporaryFile]](part)
      val multipartBody = MultipartFormData(dataParts, files, Seq[BadPart](), Seq[MissingFilePart]())

      // Setup
      gymService.get("demo").returns(Success(Demo))
      authService.isAdmin(any, any).returns(Success(true))
      photoService.getMime.returns("image/jpeg")
      photoService.generateFileName().returns("newTestPhoto.jpeg")
      photoService.upload(part.ref.file, "newTestPhoto.jpeg").returns(Promise.successful().future)
      routeService.save(any).returns(Promise.successful().future)
      request.body returns multipartBody

      // Execute
      val result = doUpload("demo")(request)

      // Assert
      status(result) must equalTo(OK)

      // Verify
      there was one(authService).isAdmin(any, any)
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