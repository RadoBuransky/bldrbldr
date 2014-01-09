package test

import play.api.mvc.Cookies
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util._
import scala.util.Failure
import play.api.mvc.Cookie
import scala.Some
import scala.language.postfixOps

object TestUtils {
  def cookies(cookies: Cookie*) = new Cookies {
    def get(name: String): Option[Cookie] = cookies.find(c => c.name == name)
    def foreach[U](f: (Cookie) => U): Unit = cookies.foreach(f(_))
  }

  def await[T](future: Future[T]): Try[T] = {
    Await.ready(future, 500 millis).value  match {
      case None => Failure(new IllegalStateException("Future should either fail or succeed."))
      case Some(t) => t
    }
  }
}