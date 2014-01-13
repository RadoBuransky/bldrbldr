package models.domain.model

import models.{JugjaneException, Color}
import models.domain.model.Grade.GradeId
import scala.util.{Failure, Try, Success}
import java.util.NoSuchElementException
import models.domain.model.Discipline.Discipline

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

  def getByName(name: String): Try[Discipline] = {
    try {
      Success(withName(name))
    }
    catch {
      case e: NoSuchElementException => Failure(e)
    }
  }
}

abstract class GradingSystem[+TGrade <: Grade](private val _name: String,
    private val _disciplines: Set[Discipline], private val _grades: Seq[TGrade]) {
  def name = _name
  def disciplines = _disciplines
  def grades = _grades
  
  def getById(id: String): Try[Grade] = {
    grades.find(grade => grade.id == id) match {
      case Some(grade) => Success(grade)
      case None => Failure(new JugjaneException("Grade doesn't exist! [" + id + "]"))
    }
  }
}

object Grade {
  type GradeId = String
}