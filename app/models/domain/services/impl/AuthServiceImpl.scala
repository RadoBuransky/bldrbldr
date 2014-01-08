package models.services.impl

import models.services.AuthServiceComponent
import play.api.mvc.Cookies
import play.api.{Configuration, Play}
import play.api.Play.current
import scala.util.{Success, Try}
import common.Utils._
import models.domain.model.Gym

trait AuthServiceComponentImpl extends AuthServiceComponent {
  lazy val authService = new AuthServiceImpl(Play.configuration)

  class AuthServiceImpl(config: Configuration) extends AuthService {
    def isAdmin(cookies: Cookies, gym: Gym): Try[Boolean] = paramsFlatTry(cookies, gym) {
      cookies.get(gym.handle) match {
        case Some(cookie) => validateSecret(cookie.value, gym.handle)
        case None => Success(false)
      }
    }

    def validateSecret(secret: String, gymHandle: String): Try[Boolean] = paramsTry(secret, gymHandle) {
      config.getString("secret." + gymHandle) match {
        case Some(s) => s == secret
        case _ => false
      }
    }
  }
}
