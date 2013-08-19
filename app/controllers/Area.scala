package controllers

import play.api._
import play.api.mvc._
import java.net.URLDecoder

// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection

// Play Json imports
import play.api.libs.json._

import play.api.Play.current
import play.api.data.format.Formats._


object Area extends Controller with MongoController {
  def collection: JSONCollection = db.collection[JSONCollection]("areas")
  
  import models._
  import models.JsonFormats._
  import play.api.data._
  import play.api.data.Forms._
  
  def newarea = Action {
    Ok(views.html.area.newarea())
  }
  
  def newareajson = Action(parse.json) { implicit request =>
    Async {
      collection.insert(request.body).map(lastError =>
        Ok("Mongo LastErorr:%s".format(lastError)))
    } 
  }
}