package models.domain.services.impl

import models.domain.services.{GymServiceComponent, RouteServiceComponent}
import scala.util.Try
import models.data.dao.RouteDaoComponent
import models.domain.{ model => dom }
import models.data.{ model => dat }
import scala.concurrent.{ExecutionContext, Future}
import models.domain.model.{Tag, Discipline, HoldsColor}
import ExecutionContext.Implicits.global
import org.joda.time.DateTime

trait RouteServiceComponentImpl extends RouteServiceComponent {
  this: RouteDaoComponent with GymServiceComponent =>

  val routeService = new RouteServiceImpl

  class RouteServiceImpl extends RouteService {
    def getByRouteId(routeId: dom.Route.RouteId): Future[dom.Route] = {
      routeDao.getByRouteId(routeId).map(routeToDomain(_).get)
    }

    def getByGymHandle(gymHandle: dom.Gym.GymHandle): Future[List[dom.Route]] = {
      routeDao.findByGymhandle(gymHandle).map { routes =>
        routes.map(routeToDomain(_).get)
      }
    }

    def delete(routeId: dom.Route.RouteId): Future[Unit] = {
      routeDao.disable(routeId)
    }

    def incFlag(routeId: dom.Route.RouteId, flagId: dom.Tag.TagId): Future[Unit] = {
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
                    route.enabled,
                    new DateTime(route._id.get.time))
                }
              }
            }
          }
        }
      }
    }
  }
}
