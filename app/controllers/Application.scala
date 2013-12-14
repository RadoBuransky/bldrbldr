package controllers

import play.api.Play.current
import play.api.db.DB
import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def untrail(path: String) = Action {
    MovedPermanently("/" + path)
  }
}