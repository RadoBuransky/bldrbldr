package models.domain.model

import Discipline.Discipline
import models.Color
import models.domain.model.Grade.GradeId

trait Grade {
  val name: String
  val id: GradeId
}
trait UnknownGrade extends Grade
trait ExactGrade extends Grade {
  def value: String
}
trait IntervalGrade extends Grade {
  def from: Grade
  def to: Grade
}
trait IndexedGrade extends Grade {
  def index: Int
}

trait ColoredGrade extends Grade
trait SingleColorGrade extends ColoredGrade {
  def color: Color
}

object Discipline extends Enumeration {
  type Discipline = Value
  val Bouldering, Climbing = Value
}

abstract class GradingSystem[+TGrade <: Grade](private val _name: String,
    private val _disciplines: Set[Discipline], private val _grades: Seq[TGrade]) {
  def name = _name
  def disciplines = _disciplines
  def grades = _grades  
  
  def findById(id: String): Option[Grade] = {
    grades.find(grade => grade.id == id)
  }
}

object Grade {
  type GradeId = String
  
  def getColor(grade: Grade): Option[Color] = {
    grade match {
      case scg: SingleColorGrade => Option(scg.color)
      case _ => None
    }
  }
}