package models.domain.services.impl

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import models.JugjaneException
import org.specs2.specification.Scope
import models.domain.gym.{Hive, Demo}

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

  trait GymServiceScope extends Scope with GymServiceComponentImpl
}
