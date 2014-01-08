package models.services

import models.domain.gym.{Demo, Hive}
import models.JugjaneException
import models.JugjaneException
import scala.util.Try
import models.domain.model.Gym

object GymService {
  private val gyms = Hive :: Demo :: Nil;

  def get(handle: String): Gym = {
    val gym = gyms.find(g => g.handle == handle)
    gym match {
      case Some(g) => g
      case None => throw new JugjaneException("Gym not found! [" + handle + "]")
    }
  }
}

trait GymServiceComponent {
  def gymService: GymService

  trait GymService {
    def get(gymHandle: String): Try[Gym]
  }
}