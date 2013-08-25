package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import org.imgscalr.Scalr
import play.api.libs.concurrent.Akka
import models.services.EmailService
import org.joda.time.DateTime
import scala.util.Random

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
    
  def getCat : String = {
    DB.withConnection { conn =>
      conn.getCatalog() }
  }
}