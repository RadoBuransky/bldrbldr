package common

import play.api.Play
import play.api.Play.current

trait Environment {
  def isDev: Boolean
  def isProd: Boolean
}

object Environment {
  implicit object PlayEnvironment extends Environment {
    def isDev: Boolean = isEnv("dev")
    def isProd: Boolean = isEnv("prod")

    private def isEnv(envName: String) = {
      Play.configuration.getString("environment") match {
        case Some(env) if env == envName => true
        case _ => false
      }
    }
  }
}