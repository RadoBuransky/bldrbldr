package test

import play.api.mvc.{Cookies, Cookie}

object TestUtils {
  def cookies(cookies: Cookie*) = new Cookies {
    def get(name: String): Option[Cookie] = cookies.find(c => c.name == name)
    def foreach[U](f: (Cookie) => U): Unit = cookies.foreach(f(_))
  }
}
