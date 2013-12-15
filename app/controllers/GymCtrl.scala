package controllers

import models.services.AuthService
import play.api.mvc.Cookie
import models.data.JsonFormats.routeFormat
import models.services.GymService
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json._
import scala.concurrent._
import models.data.Route
import scala.concurrent.ExecutionContext.Implicits.global
import models.domain.route.Tag
import models.ui.Color2

object GymCtrl extends Controller with MongoController {
  private def collection: JSONCollection = db.collection[JSONCollection]("gym")

  import models._

  def get(gymname: String, s: Option[String]) = Action.async { request =>
    // Get gym by handle
    val gym = GymService.get(gymname)

    // Get boulders from Mongo for this gym
    getBoulders(gym.handle).map {
      routes => {
        // Group routes by grade
        val routesByGrade = routes.groupBy(route => route.gradeId)

        val cookie: Cookie = {
          if ((s.isDefined) &&
              (AuthService.validateSecret(s.get, gym.handle))) {
            // Yes, there are better ways how to do authorization...
            Cookie(gymname, s.get, Some(60*60*24*7))
          }
          else {
            null
          }
        }

        val isAdmin = (cookie != null) || AuthService.isAdmin(request.cookies, gym)

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
    
  /**
   * GET - Initialization of the form for new boulder.
   * @param gymHandle
   * @return
   */
  def newBoulder(gymHandle: String) = Action {
    val gym = GymService.get(gymHandle)
    val grades = gym.gradingSystem.grades.map(g => g.id -> g.name)
    val colors = gym.holdColors.map(c => Color2(c))
    val categories = (gym.categories ::: Tag.categories).map(c => c.id -> c.name)
    Ok(views.html.route.create(grades, colors, categories, gymHandle))
  }
  
  private def getBoulders(gymhandle: String): Future[List[Route]] = {
    db.collection[JSONCollection]("route").
    	find(Json.obj("gymHandle" -> gymhandle, "enabled" -> true)).
    	sort(Json.obj("_id" -> -1)).
    	cursor[Route].collect[List](Int.MaxValue, true)
  }
}