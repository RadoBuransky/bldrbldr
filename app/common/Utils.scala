package common

import models.JugjaneException
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object Utils {
  def notEmpty(s: String, name: String) = require(!s.isEmpty, name + " cannot be empty")

  def paramsTry[T](params: Any*)(r: => T): Try[T] = paramsFlatTry(params)(Success(r))

  def paramsFlatTry[T](params: Any*)(r: => Try[T]): Try[T] =
    try r catch {
      case NonFatal(e) => {
        Failure(new JugjaneException(params.mkString("[", ",", "]"), e))
      }
    }
}
