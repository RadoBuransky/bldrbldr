package models.services

import models.domain.gym.{Demo, Gym, Hive}
import models.JugJaneException
import models.JugJaneException

object GymService {
  private val gyms = Hive :: Demo :: Nil;

  def get(handle: String): Gym = {
    val gym = gyms.find(g => g.handle == handle)
    gym match {
      case Some(g) => g
      case None => throw new JugJaneException("Gym not found! [" + handle + "]")
    }
  }
}