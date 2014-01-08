package models.data.dao

import scala.concurrent.Future
import models.domain.{model => dom}
import models.data.{model => dat}

trait RouteDaoComponent {
  def routeDao: RouteDao

  trait RouteDao {
    def getByRouteId(routeId: dom.Route.RouteId): Future[dom.Route]
    def findByGymhandle(gymhandle: String): Future[List[dat.Route]]
    def incFlag(routeId: String, flagId: dom.Tag.TagId): Unit
  }
}