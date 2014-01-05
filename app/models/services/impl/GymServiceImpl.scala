package models.services.impl

import models.domain.gym.{Demo, Hive, Gym}
import models.services.{GymService, GymServiceComponent}
import models.JugjaneException

trait GymServiceComponentImpl extends GymServiceComponent {
  val gymService: GymService = new GymServiceImpl()

  class GymServiceImpl extends GymService {
    private val gyms = Hive :: Demo :: Nil;

    def get(gymHandle: String): Gym = {
      val gym = gyms.find(g => g.handle == gymHandle)
      gym match {
        case Some(g) => g
        case None => throw new JugjaneException("Gym not found! [" + gymHandle + "]")
      }
    }
  }
}
