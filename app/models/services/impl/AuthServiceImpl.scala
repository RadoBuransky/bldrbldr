package models.services.impl

import models.services.AuthServiceComponent
import play.api.mvc.Cookies
import models.domain.gym.Gym
import play.api.{Configuration, Play}
import play.api.Play.current

trait AuthServiceComponentImpl extends AuthServiceComponent {
  lazy val authService = new AuthServiceImpl(Play.configuration)

  class AuthServiceImpl(config: Configuration) extends AuthService {
    def isAdmin(cookies: Cookies, gym: Gym): Boolean = {
      cookies.get(gym.handle) match {
        case Some(cookie) => validateSecret(cookie.value, gym.handle)
        case None => false
      }
    }

    def validateSecret(secret: String, gymHandle: String): Boolean = {
      config.getString("secret." + gymHandle) match {
        case Some(s) => s == secret
        case _ => false
      }
    }
  }
}
