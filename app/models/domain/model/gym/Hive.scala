package models.domain.gym

import models.Color
import models.domain.grade._
import java.net.URL
import java.util.Locale
import models.domain._
import models.domain.model._
import models.domain.model.Grade
import models.domain.model.GradingSystem
import models.domain.model.CategoryTag

case class HiveGrade(val from: Grade, val to: Grade, val color: Color, val name: String, val id: String)
	extends IntervalGrade with model.SingleColorGrade

object HiveGradingSystem extends GradingSystem[HiveGrade]("Hive", Set(model.Discipline.Bouldering),
    HiveGrade(Hueco.V0, Hueco.V2, Color(0, 0, 0), "⬣", "hive1") ::
    HiveGrade(Hueco.V1, Hueco.V3, Color(1, 0, 0), "⬣ ⬣", "hive2") ::
    HiveGrade(Hueco.V2, Hueco.V4, Color(0, 1, 0), "⬣ ⬣ ⬣", "hive3") ::
    HiveGrade(Hueco.V3, Hueco.V5, Color(0, 0, 1), "⬣ ⬣ ⬣ ⬣", "hive4") ::
    HiveGrade(Hueco.V4, Hueco.V6, Color(1, 1, 0), "⬣ ⬣ ⬣ ⬣ ⬣", "hive5") ::
    HiveGrade(Hueco.V5, Hueco.V7, Color(0, 1, 1), "⬣ ⬣ ⬣ ⬣ ⬣ ⬣", "hive6") ::
    Nil)
    
object Hive extends Gym {
  import HoldsColor._
  
  val name = "Hive"
  val url = new URL("http://www.hiveclimbing.com/")
  val handle = "hive"
  val gradingSystem = HiveGradingSystem
  val disciplines = Set(model.Discipline.Bouldering)
  val holdColors: List[HoldsColor] =
      List(Color.Red, Color.LightRed, Color.Orange, Color.Yellow,
      Color.Green, Color.LightGreen, Color.Blue, Color.LightBlue,
      Color.Indigo, Color.Violet, Color.White, Color.Black, Color.Brown, Color.Sand,
      (Color.Yellow, Color.Blue), (Color.White, Color.Black), (Color.Sand, Color.Black),
      (Color.Red, Color.Black), (Color.Green, Color.Black))
  val categories = List(CategoryTag("TNT"), CategoryTag("CNC"))
  val address = model.Address("520 Industrial Avenue", "Vancouver", Locale.CANADA)
}