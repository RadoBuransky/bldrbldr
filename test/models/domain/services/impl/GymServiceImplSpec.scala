package models.domain.services.impl

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import models.JugjaneException
import org.specs2.specification.Scope
import models.domain.gym.{Hive, Demo}
import models.domain.model.Gym

class GymServiceImplSpec extends Specification with Mockito {
  "get" should {
    "return demo" in new GymServiceScope {
      gymService.get("demo") must beASuccessfulTry.withValue(Demo)
    }

    "return Hive" in new GymServiceScope {
      gymService.get("hive") must beASuccessfulTry.withValue(Hive)
    }

    "throw an exception if gym doesn't exists" in new GymServiceScope {
      gymService.get("x") must beAFailedTry.withThrowable[JugjaneException]
    }
  }

  "Demo" should {
    "initialize correctly" in {
      checkGym(Demo)
    }
  }

  "Hive" should {
    "initialize correctly" in {
      checkGym(Hive)
    }
  }

  trait GymServiceScope extends Scope with GymServiceComponentImpl

  private def checkGym(gym: Gym) = {
    gym.name must not beEmpty;
    gym.url must not beNull;
    gym.url.toString must not beEmpty;
    gym.handle must not beEmpty;
    gym.gradingSystem must not beNull;
    gym.disciplines must not beEmpty;
    gym.holdColors must not beEmpty;
    gym.categories must not beEmpty;
    gym.address must not beNull;
    gym.address.street must not beEmpty;
    gym.address.city must not beEmpty;
    gym.address.country.toString must not beEmpty;
  }
}
