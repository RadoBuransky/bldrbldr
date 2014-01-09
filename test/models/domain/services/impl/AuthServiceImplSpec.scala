package models.domain.services.impl

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.{Configuration, Play}
import play.api.mvc.{Cookies, Cookie}
import models.domain.gym.Demo
import test.TestUtils

class AuthServiceImplSpec extends Specification with Mockito {
  "isAdmin" should {
    "return true if cookie contains correct secret" in new AuthServiceScope {
      // Setup
      configuration.getString("secret.demo") returns Some("123")

      val c1 = Cookie("demo", "123", Some(60*60*24*7))
      val c2 = Cookie("xxx", "abc", Some(10))
      authService.isAdmin(TestUtils.cookies(c1, c2), Demo) must beSuccessfulTry.withValue(true)
    }

    "return false if there is no cookie for the" in new AuthServiceScope {
      val c1 = Cookie("aaa", "123", Some(60*60*24*7))
      val c2 = Cookie("xxx", "abc", Some(10))
      authService.isAdmin(TestUtils.cookies(c1, c2), Demo) must beSuccessfulTry.withValue(false)
    }
  }

  "validateSecret" should {
    "return true if secret matches configuration" in new AuthServiceScope {
      // Setup
      configuration.getString("secret.demo") returns Some("qwe")
      authService.validateSecret("qwe", "demo") must beSuccessfulTry.withValue(true)
    }

    "return false if secret doesn't match configuration" in new AuthServiceScope {
      // Setup
      configuration.getString("secret.demo") returns Some("qwe")
      authService.validateSecret("666", "demo") must beSuccessfulTry.withValue(false)
    }

    "return false if there is no configuration" in new AuthServiceScope {
      // Setup
      configuration.getString("secret.fff") returns Some("qwe")
      authService.validateSecret("qwe", "aaa") must beSuccessfulTry.withValue(false)
    }
  }

  trait AuthServiceScope extends Scope with AuthServiceComponentImpl {
    lazy val configuration = mock[Configuration]
  }
}
