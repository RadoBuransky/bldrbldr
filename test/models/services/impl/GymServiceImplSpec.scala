package models.services.impl

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import models.JugjaneException
import org.specs2.specification.Scope

class GymServiceImplSpec extends Specification with Mockito {
  "get" should {
    "return gym if exists" in new GymServiceScope {
      gymService.get("demo") mustNotEqual beNull
    }

    "throw an exception if gym doesn't exists" in new GymServiceScope {
      gymService.get("x") must throwA[JugjaneException]
    }
  }

  trait GymServiceScope extends Scope with GymServiceComponentImpl
}
