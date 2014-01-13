package models.domain.services.impl

import models.domain.services.{GymServiceComponent, RouteServiceComponent}
import scala.util.Try
import models.data.dao.RouteDaoComponent
import models.domain.{ model => dom }
import models.data.{ model => dat }
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID

trait RouteServiceComponentImpl extends RouteServiceComponent {
  this: RouteDaoComponent with GymServiceComponent =>

  val routeService = new RouteServiceImpl

  class RouteServiceImpl extends RouteService {
    def getByRouteId(routeId: dom.Route.RouteId): Future[dom.Route] =
      routeDao.getByRouteId(routeId).map(datToDom(_).get)

    def getByGymHandle(gymHandle: dom.Gym.GymHandle): Future[List[dom.Route]] = {
      routeDao.findByGymhandle(gymHandle).map { routes =>
        routes.map(datToDom(_).get)
      }
    }

    def delete(routeId: dom.Route.RouteId): Future[Unit] = routeDao.disable(routeId)

    def incFlag(routeId: dom.Route.RouteId, flagId: dom.Tag.TagId): Future[Unit] =
      routeDao.incFlag(routeId, flagId)

    def save(route: dom.Route): Future[Unit] = routeDao.save(domToDat(route))

    private def datToDom(route: dat.Route): Try[dom.Route] = {
      require(route._id.isDefined, "route must have _id set")

      gymService.get(route.gymHandle).flatMap { gym =>
        dom.Route.create(Some(route._id.get.stringify), gym, route.fileName, route.gradeId,
          route.holdsColor, route.note, route.discipline, route.categories, route.flags,
          route.enabled, Some(new DateTime(route._id.get.time)))
      }
    }

    private def domToDat(route: dom.Route): dat.Route = {
      val id = route.id.map(BSONObjectID(_))
      val discipline = route.discipline.toString
      val categoryIds = route.categories.map(_.id)
      val flags = route.flags.filter(_.counter.getOrElse(0) > 0).map { flag =>
        flag.name -> flag.counter.get
      }
      dat.Route(id, route.gym.handle, route.fileName, route.grade.id, route.holdsColor.id,
        route.note, discipline, route.enabled, categoryIds, flags.toMap)
    }
  }
}
