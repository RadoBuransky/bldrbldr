package models.domain.gym

import java.net.URL
import models.domain.grade._
import models.Color
import java.util.Locale
import models.domain._
import models.domain.model._
import models.domain.model.Grade
import models.domain.model.GradingSystem
import models.domain.model.CategoryTag

case class DemoGrade(val from: Grade, val to: Grade, val color: Color, val name: String, val id: String)
  extends IntervalGrade with model.SingleColorGrade

object DemoGradingSystem extends GradingSystem[DemoGrade]("Demo", Set(model.Discipline.Bouldering),
  DemoGrade(Hueco.V0, Hueco.V1, Color.Black, "Entry", "demo1") ::
  DemoGrade(Hueco.V1, Hueco.V3, Color.Black, "Beginner", "demo2") ::
  DemoGrade(Hueco.V3, Hueco.V5, Color.Black, "Intermediate", "demo3") ::
  DemoGrade(Hueco.V5, Hueco.V7, Color.Black, "Expert", "demo4") ::
  Nil)

object Demo extends Gym {
  import HoldsColor._

  val name = "Mystery Crack Gym"
  val url = new URL("http://www.mysterycrack.com/")
  val handle = "demo"
  val gradingSystem = DemoGradingSystem
  val disciplines = Set(model.Discipline.Bouldering)
  val holdColors: List[HoldsColor] = List(Color.Red, Color.LightRed, Color.Orange, Color.Yellow,
    Color.Green, Color.LightGreen, Color.Blue, Color.LightBlue,
    Color.Indigo, Color.Violet, Color.White, Color.Black, Color.Brown, Color.Sand,
    (Color.Yellow, Color.Blue), (Color.White, Color.Black), (Color.Sand, Color.Black),
    (Color.Red, Color.Black), (Color.Green, Color.Black))
  val categories = List(CategoryTag("TNT"), CategoryTag("CNC"))
  val address = model.Address("Marie Curie Sklodowskej 39", "Bratislava", Locale.forLanguageTag("sk-sk"))
}
