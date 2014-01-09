package models.domain.services.impl

import models.domain.gym.{Demo, Hive}
import models.domain.services.{GymService, GymServiceComponent}
import models.JugjaneException
import scala.util.Try
import common.Utils._
import models.domain.model.Gym

trait GymServiceComponentImpl extends GymServiceComponent {
  val gymService: GymService = new GymServiceImpl()

  class GymServiceImpl extends GymService {
    private val gyms = Hive :: Demo :: Nil;

    def get(gymHandle: String): Try[Gym] = paramsTry(gymHandle) {
      val gym = gyms.find(g => g.handle == gymHandle)
      gym match {
        case Some(g) => g
        case None => throw new JugjaneException("Gym not found!")
      }
    }
  }
}
