package models.domain.grade

import scala.collection.immutable
import Discipline.Discipline
import scala.collection.immutable.LinearSeq
import models.Color

trait Grade {
  val name: String
  val id: String
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
  
  def getColor(grade: Grade): Option[Color] = {
    grade match {
      case scg: SingleColorGrade => Option(scg.color)
      case _ => None
    }
  }
}