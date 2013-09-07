package models.grade.system.custom

import models.grade._
import models.grade.system.Hueco

case class HiveGrade(val from: Grade, val to: Grade, val index: Int, val color: Color)
	extends IntervalGrade with IndexedGrade with SingleColorGrade

object Hive extends GradingSystem[HiveGrade]("Hive", Set(Discipline.Bouldering),
    HiveGrade(Hueco.V0, Hueco.V2, 1, Color(0, 0, 0)) ::
    HiveGrade(Hueco.V1, Hueco.V3, 2, Color(1, 0, 0)) ::
    HiveGrade(Hueco.V2, Hueco.V4, 3, Color(0, 1, 0)) ::
    HiveGrade(Hueco.V3, Hueco.V5, 4, Color(0, 0, 1)) ::
    HiveGrade(Hueco.V4, Hueco.V6, 5, Color(1, 1, 0)) ::
    HiveGrade(Hueco.V5, Hueco.V7, 6, Color(0, 1, 1)) ::
    Nil )