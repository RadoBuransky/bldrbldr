package models.data

import models.data.model.Route
import scala.concurrent.Future

trait RouteDaoComponent {
  def routeDao: RouteDao

  trait RouteDao {
    def findByGymhandle(gymhandle: String): Future[List[Route]]
    def incFlag(routeId: String, flagId: String): Unit
  }
}