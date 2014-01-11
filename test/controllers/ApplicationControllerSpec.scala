package controllers

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import com.jugjane.controllers.Application
import play.mvc.Http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ApplicationControllerSpec extends Specification with Mockito {
  "index" should {
    "be ok" in {
      val result = Application.index(FakeRequest(GET, "/"))
      status(result) must equalTo(Status.OK)
    }
  }

  "untrail" should {
    "redirect" in {
      val result = Application.untrail("xxx")(FakeRequest(GET, "/xxx/"))
      status(result) must equalTo(Status.MOVED_PERMANENTLY)
      redirectLocation(result) must equalTo(Some("/xxx"))
    }
  }

  "climbing" should {
    "see other" in {
      val result = Application.climbing(FakeRequest(GET, "/climbing"))
      status(result) must equalTo(Status.SEE_OTHER)
      redirectLocation(result) must equalTo(Some("/"))
    }
  }
}
