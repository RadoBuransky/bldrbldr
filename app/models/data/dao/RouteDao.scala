package models.data.dao

import scala.concurrent.Future
import models.domain.{model => dom}
import models.data.{model => dat}

trait RouteDaoComponent {
  def routeDao: RouteDao

  trait RouteDao {
    def getByRouteId(routeId: String): Future[dat.Route]
    def disable(routeId: String): Future[Unit]
    def findByGymhandle(gymHandle: String): Future[List[dat.Route]]
    def incFlag(routeId: String, flagId: String): Future[Unit]
  }
}