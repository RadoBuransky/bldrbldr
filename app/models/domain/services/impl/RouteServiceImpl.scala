package models.domain.services.impl

import models.domain.services.{GymServiceComponent, RouteServiceComponent}
import scala.util.{Success, Failure, Try}
import common.Utils._
import models.data.dao.RouteDaoComponent
import models.domain.{ model => dom }
import models.data.{ model => dat }
import scala.concurrent.{ExecutionContext, Promise, Future}
import models.JugjaneException
import models.domain.model.{Tag, Discipline, HoldsColor}
import ExecutionContext.Implicits.global

trait RouteServiceComponentImpl extends RouteServiceComponent {
  this: RouteDaoComponent with GymServiceComponent =>

  val routeService = new RouteServiceImpl

  class RouteServiceImpl extends RouteService {
    def getByRouteId(routeId: dom.Route.RouteId): Future[dom.Route] =
      routeDao.getByRouteId(routeId).map(routeToDomain(_).get)

    def delete(routeId: dom.Route.RouteId): Try[Unit] = paramsTry(routeId) {
      notEmpty(routeId, "routeId")
    }

    def incFlag(routeId: dom.Route.RouteId, flagId: dom.Tag.TagId): Try[Unit] = paramsTry(routeId, flagId) {
      routeDao.incFlag(routeId, flagId)
    }

    private def routeToDomain(route: dat.Route): Try[dom.Route] = {
      require(route._id.isDefined, "route must have _id set")

      gymService.get(route.gymHandle).flatMap { gym =>
        gym.gradingSystem.getById(route.gradeId).flatMap { grade =>
          HoldsColor.getById(gym.holdColors, route.holdsColor).flatMap { holdsColor =>
            Discipline.getByName(route.discipline).flatMap { discipline =>
              Tag.getCategoriesByIds(route.categories, gym.categories).flatMap { categories =>
                Tag.getFlagsByIdsCounts(route.flags).map { flags =>
                  dom.Route(
                    route._id.get.stringify,
                    gym,
                    route.fileName,
                    grade,
                    holdsColor,
                    route.note,
                    discipline,
                    categories,
                    flags,
                    route.enabled)
                }
              }
            }
          }
        }
      }
    }
  }
}
