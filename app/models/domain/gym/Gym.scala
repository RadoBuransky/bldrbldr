package models.domain.gym

import models.domain.grade.GradingSystem
import models.domain.grade.Grade
import models.domain.grade.Discipline.Discipline
import java.net.URI
import java.net.URL

abstract class Gym {
  def name: String
  def url: URL
  def handle: String
  def secret: String
  def gradingSystem: GradingSystem[Grade]
  def disciplines: Set[Discipline]
  def holdColors: Set[ColoredHolds]
}