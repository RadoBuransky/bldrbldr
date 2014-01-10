package models.domain.model

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import models.domain.gym.{DemoGrade, DemoGradingSystem}
import models.domain.grade.Hueco
import models.{JugjaneException, Color}
import java.util.NoSuchElementException

class GradingSystemSpec extends Specification with Mockito {
  "GradingSystem" should {
    "return grade for existing grade id" in {
      DemoGradingSystem.getById("demo2") must beASuccessfulTry.withValue(
        DemoGrade(Hueco.V1, Hueco.V3, Color.Black, "Beginner", "demo2"))
    }

    "fail for nonexisting grade id" in {
      DemoGradingSystem.getById("x") must beAFailedTry.withThrowable[JugjaneException]
    }
  }

  "Discipline" should {
    "get valid discipline by name" in {
      Discipline.getByName("Bouldering") must beASuccessfulTry.withValue(Discipline.Bouldering)
    }

    "fail for nonexisting discipline name" in {
      Discipline.getByName("running") must beAFailedTry.withThrowable[NoSuchElementException]
    }
  }
}
