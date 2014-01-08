package models.domain.model

import Discipline.Discipline
import java.net.URL
import java.util.Locale
import models.domain.model.Gym.GymHandle

trait Gym {
  def name: String
  def url: URL
  def handle: GymHandle
  def gradingSystem: GradingSystem[Grade]
  def disciplines: Set[Discipline]
  def holdColors: List[HoldsColor]
  def categories: List[CategoryTag]
  def address: Address
}

case class Address(
  val street: String,
  val city: String,
  val country: Locale)

object Gym {
  type GymHandle = String
}