package models.gym

import models.grade.GradingSystem
import models.grade.Grade
import models.grade.Discipline.Discipline

abstract class Gym[+TGrade <: Grade] {
  def name: String
  def gradingSystem: GradingSystem[TGrade]
  def disciplines: Set[Discipline]
  def holdColors: Set[ColoredHolds]
}