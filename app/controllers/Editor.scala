package controllers

import play.api.mvc.Controller
import play.api.mvc.Action

object Editor extends Controller {
  def index = Action {
	lazy val fib: Stream[Int] = 0 #:: 1 #:: (fib zip fib.tail map (x => x._1 + x._2))
	val i = fib.take(1)
    Ok(views.html.editor())
  }
}