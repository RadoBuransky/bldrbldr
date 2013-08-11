package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import org.imgscalr.Scalr

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