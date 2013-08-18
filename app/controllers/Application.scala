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
  
  def upload = Action(parse.multipartFormData) {
    request => { }
    Ok("Ok!")
  }
  
  def msg(title: String, text: String, action: String, error: Boolean) = Action {
    Ok(views.html.message(title, text, action, error))
  }
  
  def getCat : String = {
    DB.withConnection { conn =>
      conn.getCatalog() }
  }
}