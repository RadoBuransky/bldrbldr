package controllers

import scala.util.Random
import org.joda.time.DateTime
import models.data.JsonFormats.gymFormat
import models.data.JsonFormats.routeFormat
import models.services.EmailService
import models.services.GymService
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
import models.domain.gym.Hive
import play.api.cache.Cached
import play.api.Play.current
import models.domain.grade.Grade
import models.domain.grade.NamedGrade
import models.domain.gym.SingleColoredHolds
import models.domain.gym.DoubleColoredHolds
import models.data.Gym
import models.data.Route
import java.util.Date
import org.joda.time.Days

object GymCtrl extends Controller with MongoController {
  private def collection: JSONCollection = db.collection[JSONCollection]("gym")

  import models._
  
  def get(gymname: String) = Action {
    Async {
      // Get gym by handle
	    val gym = GymService.get(gymname)
	    
	    // Get boulders from Mongo for this gym
	    getBoulders(gym.handle).map {
	      routes => {
	        // Group routes by grade
	        val routesByGrade = routes.groupBy(route => route.grade).toList.
	        	sortBy(gradeGroup => gradeGroup._1)	        
	        
	        // Serialize routes to JSON
	        val routesJson = routesByGrade.map(gradeGroup => {
	          Json.obj("grade" -> gradeGroup._2(0).grade,
	              "routes" -> routesToJson(gradeGroup._2))
          }).toArray
	        
	        // Create result
			    Ok(Json.obj(
			    	"name" -> gym.name,
			    	"url" -> gym.url.toString(),
			    	"routes" -> routesJson))        
	      }
	    }
    }
  }
  
  private def routesToJson(routes: List[Route]): List[JsObject] = {
    routes match {
      case route :: tail => routeToJson(route) :: routesToJson(tail)
      case Seq() => Nil
    }
  }
  
  private def routeToJson(route: Route): JsObject = {
    val days = Days.daysBetween(new DateTime(route._id.get.time), DateTime.now()).getDays()
    Json.obj("id" -> route._id.get.stringify,
        "holdcolor" -> route.holdcolor,
        "note" -> route.note,
        "days" -> days)
  }
  
  private def getBoulders(gymhandle: String): Future[List[Route]] = {
    db.collection[JSONCollection]("route").
    	find(Json.obj("gymhandle" -> gymhandle)).
    	cursor[Route].toList
  }
    
  /**
   * GET - Initialization of the form for new boulder.
   * @param gymname
   * @return
   */
  def newBoulder(gymname: String) = Action {
    val gym = GymService.get(gymname)
    Ok(Json.obj("grades" -> gradesToJson(gym), "holds" -> holdsToJson(gym)))
  }
  
  def grades(gymname: String) = Action {
  	Ok(Json.toJson(Hive.gradingSystem.grades.zipWithIndex.map {
    	case (grade, index) => Json.obj("id" -> index, "name" -> grade.name)
  	}))
  }
  
  def list() = Action {
    Async {
      collection.find(Json.obj("validated" -> true, "approved" -> true, "disabled" -> false),
      	Json.obj("gymname" -> 1)).sort(Json.obj("gymname" -> 1)).cursor[JsObject].toList.map {
          gyms => Ok(Json.toJson(gyms.map( gym =>
            Json.obj("id" -> (gym \ "_id" \ "$oid"), "gymname" -> (gym \ "gymname") ) ))) }
    }
  }

  def newGym = Action(parse.json) {
    request => Async {
		  val gym = request.body.as[models.data.Gym]
		
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
  
  private def holdsToJson(gym: models.domain.gym.Gym) = {
    Json.toJson(gym.holdColors.map {
      holdColor => holdColor match {
    		case (SingleColoredHolds(color)) =>
    		  Json.obj("name" -> holdColor.name, "color" -> color.toWeb)
  		  case (DoubleColoredHolds(color1, color2)) =>
    		  Json.obj("name" -> holdColor.name, "color" -> color1.toWeb, "color2" -> color2.toWeb)
      }
  	})
  }
  
  private def gradesToJson(gym: models.domain.gym.Gym) = {
    Json.toJson(gym.gradingSystem.grades.zipWithIndex.map {
    	case (grade, index) => {
    	  grade match {
    	    case namedGrade: NamedGrade =>  Json.obj("id" -> index, "name" -> namedGrade.name)
    	  }
    	}
  	})
  }
  
  private def createNewGym(gym: Gym): Result = {  
    // Set default values
    val updatedGym = gym.copy(
      handle = Option(gym.gymname.toLowerCase().filter(c => c != ' ')),
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