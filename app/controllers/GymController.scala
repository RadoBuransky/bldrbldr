package com.jugjane.controllers

import models.domain.services._
import play.api.mvc.Action
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext.Implicits.global
import models.ui.Color2
import models.ui
import models.domain.model.Tag
import scala.util.Success
import scala.util.Failure
import play.api.mvc.Cookie
import scala.Some
import scala.concurrent.Promise
import play.api.Logger
import common.SupportedLang._
import play.api.i18n.Lang
import com.jugjane.common.Messages

trait GymController extends Controller {
  this: GymServiceComponent with AuthServiceComponent with RouteServiceComponent
    with PhotoServiceComponent =>

  def get(gymHandle: String, s: Option[String]) = Action.async { request =>
    // Get gym by handle
    gymService.get(gymHandle) match {
      case Success(gym) => {
        routeService.getByGymHandle(gymHandle).map { routes =>
          // Admin authorizaton
          val authCookie = createAuthCookie(s, gymHandle)
          val isAdmin = authCookie.map(c => Some(true)).getOrElse {
            authService.isAdmin(request.cookies, gym) match {
              case Success(r) => Some(r)
              case Failure(f) => throw f
            }
          }.getOrElse(false)

          implicit val lang: Lang = gym.address.country

          val routesByGrade = routes.groupBy(route => route.grade.id)
          val uiGrades = gym.gradingSystem.grades.filter(g =>
            routesByGrade.get(g.id).isDefined).map(g => ui.Grade(g)).toList
          val uiRoutes = routesByGrade.map(e => (e._1, e._2.map(r => {
            val photoUrl = r.fileName.map(photoService.getUrl(_, gymHandle).toString)
            ui.Route(r, photoUrl)
          })))
          val result = Ok(views.html.gym.index(ui.Gym(gym, uiGrades, uiRoutes), isAdmin)(gym.address.country))

          authCookie match {
            case Some(cookie) => result.withCookies(cookie)
            case None => result
          }
        }
      }
      case Failure(f) => {
        Logger.error("GymController.get [" + gymHandle + "," + s + "]", f)
        Promise.successful(InternalServerError).future
      }
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
        implicit val lang: Lang = gym.address.country

        val grades = gym.gradingSystem.grades.map(g => g.id -> g.name)
        val colors = gym.holdColors.map(c => Color2(c))
        val categories = (gym.categories ::: Tag.categories).map(c => c.id -> Messages.Models.category(c.id))
        Ok(views.html.route.create(grades, colors, categories, gymHandle)(gym.address.country))
      }
      case Failure(f) => InternalServerError
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