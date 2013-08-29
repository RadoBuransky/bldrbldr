package controllers

import scala.util.Random
import org.joda.time.DateTime
import models.JsonFormats.gymFormat
import models.services.EmailService
import play.api.Logger
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import play.api.libs.json._
import play.api.mvc.Result
import play.api.mvc.Request
import org.codehaus.jackson.annotate.JsonValue
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent._
import scala.util.Try

object Gym extends Controller with MongoController {
  private def collection: JSONCollection = db.collection[JSONCollection]("gym")

  import models._
  
  def list() = Action {
    Async {
      collection.find(Json.obj("validated" -> true, "approved" -> true, "disabled" -> false),
      	Json.obj("gymname" -> 1)).sort(Json.obj("gymname" -> 1)).cursor[JsObject].toList.map {
          gyms => Ok(Json.toJson(gyms.map( gym =>
            Json.obj("id" -> (gym \ "_id" \ "$oid"), "gymname" -> (gym \ "gymname") ) ))) }
    }
  }

  def newGym = Action(parse.json) { request =>
    Async {
	  val gym = request.body.as[models.Gym]
	
	  validateNewGym(gym).map { msg =>
	    msg match {
	      case s: String => {
	        Logger.error(s)
	        BadRequest(s)
	      }
	      case _ => createNewGym(gym)
	    }
	  }
    }
  }
  
  def validate(secret: String) = Action {
    Async {      
      collection.update(Json.obj("secret" -> secret),
          Json.obj( "$set" -> Json.obj("validated" -> true))).map {
        lastError => if (lastError.ok) {
          Ok
        }
        else {
          Logger.error(lastError.errMsg.get)
          BadRequest("Cannot validate the gym!")
        }        
      }
    }
  }
  
  def approve(secret: String) = Action {
    Async {      
      collection.update(Json.obj("secret" -> secret),
          Json.obj( "$set" -> Json.obj("approved" -> true))).map {
        lastError => if (lastError.ok) {
          Ok
        }
        else {
          Logger.error(lastError.errMsg.get)
          BadRequest("Cannot approve the gym!")
        }        
      }
    }
  }
  
  private def createNewGym(gym: Gym): Result = {  
    // Set default values
    val updatedGym = gym.copy(
      created = Option(DateTime.now),
      validated = Option(false),
      disabled = Option(false),
      approved = Option(false),
      secret = Option(Random.nextLong.abs.toString))  
    
    // Insert to Mongo
    collection.insert(updatedGym);    
    Logger.debug("New gym inserted to Mongo.");
    
    // Send emails asynchronously
    EmailService.newGym(gym.email, updatedGym)
    EmailService.newGymNotif(updatedGym)
    
    Ok
  }
  
  private def validateNewGym(gym: Gym): Future[String] = {    
    collection.find(Json.obj("gymname" -> gym.gymname)).cursor[Gym].toList.map {
      list => if (!list.isEmpty) {
        "Gym with the same name already exists!"
      }
      else {
        null
      }
    }
  }
}