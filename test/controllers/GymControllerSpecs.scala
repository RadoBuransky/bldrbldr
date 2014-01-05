package controllers

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import models.data.RouteDaoComponent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models.JugjaneException

class GymControllerSpecs extends Specification with Mockito {
  "newBoulder" should {
    "fail for nonexisting gym" in {
      gymController.newBoulder("xxx")(FakeRequest(GET, "/climbing/xxx")) must throwA[JugjaneException]
    }

    "succeed for existing gym" in {
      val result = gymController.newBoulder("demo")(FakeRequest(GET, "/climbing/demo"))
      status(result) must equalTo(200)
    }
  }

  private val gymController = new GymController with RouteDaoComponent {
    def routeDao: RouteDao = mock[RouteDao]
  }
}
