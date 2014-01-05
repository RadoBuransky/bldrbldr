package models.services

import models.domain.gym.Gym
import play.api.mvc.Cookies
import play.api.Play
import play.api.Play.current

object AuthService {
  def isAdmin(cookies: Cookies, gym: Gym): Boolean = {
    cookies.get(gym.handle) match {
      case Some(cookie) => validateSecret(cookie.value, gym.handle)
      case None => false
    }
  }

  def validateSecret(secret: String, gymHandle: String): Boolean = {
    Play.configuration.getString("secret." + gymHandle) match {
      case Some(s) => s == secret
      case _ => false
    }
  }
}

trait AuthServiceComponent {
  def authService: AuthService

  trait AuthService {
    def isAdmin(cookies: Cookies, gym: Gym): Boolean
    def validateSecret(secret: String, gymHandle: String): Boolean
  }
}