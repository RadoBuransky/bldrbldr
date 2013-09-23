package models.services

import models.domain.gym.Gym
import models.JugJaneException
import models.domain.gym.Hive
import models.JugJaneException

object GymService {
  def authorize(handle: String, secret: String): Boolean = {   
    get(handle).secret == secret
  }
  
  def get(handle: String): Gym = {
    // Currently pretty hardcoded
    val hiveHandle = Hive.handle
    handle match {
      case `hiveHandle` => Hive
      case _ => throw new JugJaneException("Gym not found! [" + handle + "]")
    }
  }
}