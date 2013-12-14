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
import play.api.libs.json._
import play.api.mvc.Result
import scala.concurrent._
import models.data.Gym
import models.data.Route
import models.domain.gym._
import models.contract.JsonMapper
import scala.concurrent.ExecutionContext.Implicits.global
import models.domain.route.Tag
import play.api.data.Form
import play.api.data.Forms._
import models.ui.Color2

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
	        val routesByGrade = routes.groupBy(route => route.gradeId)

	        // Serialize routes to JSON
	        val routesByGradeEx = routesByGrade.map(gradeGroup => {
	        	val grade =	gym.gradingSystem.findById(gradeGroup._2(0).gradeId).get
            (grade, gradeGroup._2)
          })

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
//			    val result = Ok(Json.obj(
//			    	"gym" -> JsonMapper.gymToJson(gym),
//			    	"gradeGroups" -> routesJson,
//			    	"isAdmin" -> isAdmin))

          val uiGrades = gym.gradingSystem.grades.filter(g =>
            routesByGrade.get(g.id).isDefined).map(g => ui.Grade(g)).toList
          val uiRoutes = routesByGrade.map(e => (e._1, e._2.map(r => ui.Route(r, gym))))
          val result = Ok(views.html.gym.index(ui.Gym(gym, uiGrades, uiRoutes), isAdmin))

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
  def newBoulder(gymHandle: String) = Action {
    val gym = GymService.get(gymHandle)
    val grades = gym.gradingSystem.grades.map(g => g.id -> g.name)
    val colors = gym.holdColors.map(c => Color2(c))
    val categories = (gym.categories ::: Tag.categories).map(c => c.id -> c.name)
    Ok(views.html.route.create(grades, colors, categories, gymHandle))
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
    	find(Json.obj("gymHandle" -> gymhandle, "enabled" -> true)).
    	sort(Json.obj("_id" -> -1)).
    	cursor[Route].collect[List](Int.MaxValue, true)
  }
  
  private def holdsToJson(gym: models.domain.gym.Gym) = {
    Json.toJson(gym.holdColors.map { h => JsonMapper.holdColorToJson(h) })
  }
  
  private def gradesToJson(gym: models.domain.gym.Gym) = {
    Json.toJson(gym.gradingSystem.grades.map { grade =>
    	Json.obj("id" -> grade.id, "name" -> grade.name)
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