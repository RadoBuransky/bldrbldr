package controllers

import org.joda.time.DateTime
import play.api._
import play.api.Play.current
import play.api.data.format.Formats._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api._
import models.services.EmailService

object Gym extends Controller with MongoController {
  def collection: JSONCollection = db.collection[JSONCollection]("gym")

  import models._
  import models.JsonFormats._
  import play.api.data._
  import play.api.data.Forms._

  def newareajson = Action(parse.json) { request =>
    // Parse
    val gym = request.body.as[Gym]
    
    // Set default values
    val updatedGym = gym.copy(
      created = Option(DateTime.now),
      validated = Option(false),
      disabled = Option(false))      
	    
    // Insert to Mongo
    collection.insert(updatedGym);
    
    // Send email asynchronously
    EmailService.send
    
    Ok
  }
}