package models.domain.model.grade

import models.domain.model.{Discipline, GradingSystem, ExactGrade}

case class UiaaGrade(val value: String) extends ExactGrade {
  val name = value
  val id = value
}

object Uiaa extends GradingSystem[UiaaGrade]("UIAA", Set(Discipline.Climbing),
  UiaaGrade("III") ::
  UiaaGrade("IV") ::
  UiaaGrade("IV/IV+") ::
  UiaaGrade("IV+") ::
  UiaaGrade("IV+/V") ::
  UiaaGrade("V") ::
  UiaaGrade("V/V+") ::
  UiaaGrade("V+") ::
  UiaaGrade("V+/VI-") ::
  UiaaGrade("VI-") ::
  UiaaGrade("VI-/VI") ::
  UiaaGrade("VI") ::
  UiaaGrade("VI/VI+") ::
  UiaaGrade("VI+") ::
  UiaaGrade("VI+/VII-") ::
  UiaaGrade("VII-") ::
  UiaaGrade("VII-/VII") ::
  UiaaGrade("VII") ::
  UiaaGrade("VII/VII+") ::
  UiaaGrade("VII+") ::
  UiaaGrade("VII+/VIII-") ::
  UiaaGrade("VIII-") ::
  UiaaGrade("VIII-/VIII") ::
  UiaaGrade("VIII") ::
  UiaaGrade("VIII/VIII+") ::
  UiaaGrade("VIII+") ::
  UiaaGrade("VIII+/IX-") ::
  UiaaGrade("IX-") ::
  UiaaGrade("IX-/IX") ::
  UiaaGrade("IX") ::
  UiaaGrade("IX/IX+") ::
  UiaaGrade("IX+") ::
  UiaaGrade("IX+/X") ::
  UiaaGrade("X-") ::
  UiaaGrade("X-/X") ::
  UiaaGrade("X") ::
  UiaaGrade("X/X+") ::
  UiaaGrade("X+") ::
  Nil) {

}
