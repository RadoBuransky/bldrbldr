package controllers

import models.services.{AuthServiceComponent, GymServiceComponent}
import play.api.mvc.Cookie
import play.api.mvc.Action
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext.Implicits.global
import models.ui.Color2
import models.ui
import scala.util.{Failure, Success}
import scala.concurrent.Future
import models.domain.model.Tag
import models.data.dao.RouteDaoComponent

trait GymController extends Controller {
  this: RouteDaoComponent with GymServiceComponent with AuthServiceComponent =>

  def get(gymHandle: String, s: Option[String]) = Action.async { request =>
    // Get gym by handle
    gymService.get(gymHandle) match {
      case Success(gym) => {
        // Get boulders from Mongo for this gym
        routeDao.findByGymhandle(gym.handle).map {
          routes => {
            // Admin authorizaton
            val authCookie = createAuthCookie(s, gymHandle)
            val isAdmin = authCookie.map(c => Some(true)).getOrElse {
              authService.isAdmin(request.cookies, gym) match {
                case Success(r) => Some(r)
                case Failure(f) => throw f
              }
            }.getOrElse(false)

            val routesByGrade = routes.groupBy(route => route.gradeId)
            val uiGrades = gym.gradingSystem.grades.filter(g =>
              routesByGrade.get(g.id).isDefined).map(g => ui.Grade(g)).toList
            val uiRoutes = routesByGrade.map(e => (e._1, e._2.map(r => ui.Route(r, gym))))
            val result = Ok(views.html.gym.index(ui.Gym(gym, uiGrades, uiRoutes), isAdmin))

            authCookie match {
              case Some(cookie) => result.withCookies(cookie)
              case None => result
            }
          }
        }
      }
      case Failure(f) => throw f
    }
  }
    
  /**
   * GET - Initialization of the form for new boulder.
   * @param gymHandle
   * @return
   */
  def newBoulder(gymHandle: String) = Action {
    gymService.get(gymHandle) match {
      case Success(gym) => {
        val grades = gym.gradingSystem.grades.map(g => g.id -> g.name)
        val colors = gym.holdColors.map(c => Color2(c))
        val categories = (gym.categories ::: Tag.categories).map(c => c.id -> c.name)
        Ok(views.html.route.create(grades, colors, categories, gymHandle))
      }
      case f: Failure[_] => throw f.exception
    }
  }

  private def createAuthCookie(secretOption: Option[String], gymHandle: String): Option[Cookie] = {
    secretOption match {
      case Some(secret) => {
        authService.validateSecret(secret, gymHandle) match {
          case Success(result) if result => Some(Cookie(gymHandle, secret, Some(60*60*24*7)))
          case _ => None
        }
      }
      case _ => None
    }
  }
}