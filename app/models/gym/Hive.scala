package models.gym

import models.Color
import models.grade.Discipline
import models.grade.Grade
import models.grade.GradingSystem
import models.grade.Hueco
import models.grade.IntervalGrade
import models.grade.NamedGrade
import models.grade.SingleColorGrade

case class HiveGrade(val from: Grade, val to: Grade, val color: Color, val name: String)
	extends IntervalGrade with SingleColorGrade with NamedGrade

object HiveGradingSystem extends GradingSystem[HiveGrade]("Hive", Set(Discipline.Bouldering),
    HiveGrade(Hueco.V0, Hueco.V2, Color(0, 0, 0), "⬣") ::
    HiveGrade(Hueco.V1, Hueco.V3, Color(1, 0, 0), "⬣⬣") ::
    HiveGrade(Hueco.V2, Hueco.V4, Color(0, 1, 0), "⬣⬣⬣") ::
    HiveGrade(Hueco.V3, Hueco.V5, Color(0, 0, 1), "⬣⬣⬣⬣") ::
    HiveGrade(Hueco.V4, Hueco.V6, Color(1, 1, 0), "⬣⬣⬣⬣⬣") ::
    HiveGrade(Hueco.V5, Hueco.V7, Color(0, 1, 1), "⬣⬣⬣⬣⬣⬣") ::
    Nil)
    
object Hive extends Gym[HiveGrade] {
  import models.gym.ColoredHolds._
  
  def name = "Hive"
  def gradingSystem = HiveGradingSystem
  def disciplines = Set(Discipline.Bouldering)
  def holdColors = Set(Color.Red, Color.Green, Color.Blue, (Color.Yellow, Color.Blue),Color.Purple,
      Color.White, Color.Yellow)
}