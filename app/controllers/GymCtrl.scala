package controllers

import scala.util.Random
import models.services.AuthService
import play.api.mvc.Cookie
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
import models.domain.grade.GradingSystem
import models.domain.gym._
import models.contract.JsonMapper
import models.domain.grade.IdGrade
import scala.concurrent.ExecutionContext.Implicits.global
import models.domain.route.Tag

object GymCtrl extends Controller with MongoController {
  private def collection: JSONCollection = db.collection[JSONCollection]("gym")

  import models._

  def get(gymname: String, s: Option[String]) = Action { request =>
    Async {      
      // Get gym by handle
	    val gym = GymService.get(gymname)
	    
	    // Get boulders from Mongo for this gym
	    getBoulders(gym.handle).map {
	      routes => {
	        // Group routes by grade
	        val routesByGrade = routes.groupBy(route => route.gradeId).toList.
	        	sortBy(gradeGroup => gradeGroup._1)
	        
	        // Serialize routes to JSON
	        val routesJson = routesByGrade.map(gradeGroup => {
	        	val route =	gym.gradingSystem.findById(gradeGroup._2(0).gradeId)
	        	Json.obj("grade" -> JsonMapper.gradeToJson(route),
	              "routes" -> routesToJson(gym, gradeGroup._2))
          }).toArray
			    	
			    val cookie: Cookie = {
				    if ((s.isDefined) &&
				        (gym.secret == s.get)) {
				      // Yes, there are better ways how to do authorization...
				      Cookie(gymname, s.get, Some(60*60*24*7))
				    }
				    else {
				      null
				    }
	        }
	        
	        val isAdmin = (cookie != null) || AuthService.isAdmin(request.cookies, gym)
	        
	        // Create result
			    val result = Ok(Json.obj(
			    	"gym" -> JsonMapper.gymToJson(gym),
			    	"gradeGroups" -> routesJson,
			    	"isAdmin" -> isAdmin))
	        
	        if (cookie == null) {
	          result
	        }
          else {
            result.withCookies(cookie)
          }
	      }
	    }
    }
  }
    
  /**
   * GET - Initialization of the form for new boulder.
   * @param gymname
   * @return
   */
  def newBoulder(gymname: String) = Action {
    val gym = GymService.get(gymname)
    Ok(Json.obj("grades" -> gradesToJson(gym),
      "holds" -> holdsToJson(gym),
      "tags" -> JsonMapper.tagsToJson(gym.tags ::: Tag.getCategories)))
  }
  
  def grades(gymname: String) = Action {
  	Ok(Json.toJson(Hive.gradingSystem.grades.zipWithIndex.map {
    	case (grade, index) => Json.obj("id" -> index, "name" -> grade.name)
  	}))
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
  
  private def routesToJson(gym: models.domain.gym.Gym, routes: List[Route]): List[JsObject] = {
    routes match {
      case route :: tail => JsonMapper.routeToJson(gym, route) :: routesToJson(gym, tail)
      case Seq() => Nil
    }
  }  
  
  private def getBoulders(gymhandle: String): Future[List[Route]] = {
    db.collection[JSONCollection]("route").
    	find(Json.obj("gymName" -> gymhandle, "enabled" -> true)).
    	sort(Json.obj("_id" -> -1)).
    	cursor[Route].collect[List](Int.MaxValue, true)
  }
  
  private def holdsToJson(gym: models.domain.gym.Gym) = {
    Json.toJson(gym.holdColors.map { h => JsonMapper.holdColorToJson(h) })
  }
  
  private def gradesToJson(gym: models.domain.gym.Gym) = {
    Json.toJson(gym.gradingSystem.grades.map {
    	case idGrade: IdGrade => {
    	  idGrade match {
    	    case namedGrade: NamedGrade =>  Json.obj("id" -> idGrade.id, "name" -> namedGrade.name)
    	  }
    	}
  	})
  }
  
  private def createNewGym(gym: Gym): Result = {  
    // Set default values
    val updatedGym = gym.copy(
      handle = Option(gym.gymName.toLowerCase().filter(c => c != ' ')),
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
    collection.find(Json.obj("gymname" -> gym.gymName)).cursor[Gym].collect[List](Int.MaxValue, true).map {
      list => if (!list.isEmpty) {
        "Gym with the same name already exists!"
      }
      else {
        null
      }
    }
  }
}