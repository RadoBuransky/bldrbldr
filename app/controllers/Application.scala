package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

object Application extends Controller {
  
  def index = Action {
    var cat = "xx"
    DB.withConnection(conn => {
    	cat = conn.getCatalog()
    })
    Ok(views.html.index(cat))
  }
  
}