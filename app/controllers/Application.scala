package controllers

import play.api.Play.current
import play.api.db.DB
import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }
  
  def about = Action {
    Ok(views.html.about())
  }
    
  def getCat : String = {
    DB.withConnection { conn =>
      conn.getCatalog() }
  }
}