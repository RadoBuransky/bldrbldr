package models.domain.gym

import models.Color
import models.domain.grade._
import models.domain.gym._
import java.net.URL

case class HiveGrade(val from: Grade, val to: Grade, val color: Color, val name: String, val id: String)
	extends IntervalGrade with SingleColorGrade with NamedGrade with IdGrade

object HiveGradingSystem extends GradingSystem[HiveGrade]("Hive", Set(Discipline.Bouldering),
    HiveGrade(Hueco.V0, Hueco.V2, Color(0, 0, 0), "⬣", "hive1") ::
    HiveGrade(Hueco.V1, Hueco.V3, Color(1, 0, 0), "⬣⬣", "hive2") ::
    HiveGrade(Hueco.V2, Hueco.V4, Color(0, 1, 0), "⬣⬣⬣", "hive3") ::
    HiveGrade(Hueco.V3, Hueco.V5, Color(0, 0, 1), "⬣⬣⬣⬣", "hive4") ::
    HiveGrade(Hueco.V4, Hueco.V6, Color(1, 1, 0), "⬣⬣⬣⬣⬣", "hive5") ::
    HiveGrade(Hueco.V5, Hueco.V7, Color(0, 1, 1), "⬣⬣⬣⬣⬣⬣", "hive6") ::
    Nil)
    
object Hive extends Gym {
  import models.domain.gym.ColoredHolds._
  
  def name = "Hive"
  val url = new URL("http://www.hiveclimbing.com/")
  def handle = "hive"
  def secret = "666"
  def gradingSystem = HiveGradingSystem
  def disciplines = Set(Discipline.Bouldering)
  def holdColors = Set(Color.Red, Color.Green, Color.Blue, (Color.Yellow, Color.Blue),Color.Purple,
      Color.White, Color.Yellow)
}