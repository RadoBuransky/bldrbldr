package controllers

import scala.annotation.implicitNotFound

import org.joda.time.DateTime

import models.JsonFormats.gymFormat
import models.services.EmailService
import play.api.Logger
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object Gym extends Controller with MongoController {
  private def collection: JSONCollection = db.collection[JSONCollection]("gym")

  import models._

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
    Logger.debug("New gym inserted to Mongo.");
    
    // Send email asynchronously
    EmailService.newGym("radoburansky@gmail.com")
    
    Ok
  }
}