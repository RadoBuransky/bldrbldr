package models.services

import models.domain.gym.Gym
import play.api.mvc.Cookies

object AuthService {
  def isAdmin(cookies: Cookies, gym: Gym): Boolean = {
    cookies.get(gym.handle) match {
      case Some(cookie) => cookie.value == gym.secret
      case None => false
    }
  }
}