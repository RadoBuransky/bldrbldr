package controllers

import play.api.mvc.Controller
import play.api.mvc.Action

object Editor extends Controller {
  def index = Action {   
    Ok(views.html.editor())
  }
}