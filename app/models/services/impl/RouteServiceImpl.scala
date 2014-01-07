package models.services.impl

import models.services.RouteServiceComponent
import models.data.RouteDaoComponent
import scala.util.Try
import common.Utils._

trait RouteServiceComponentImpl extends RouteServiceComponent {
  this: RouteDaoComponent =>

  val routeService = new RouteServiceImpl

  class RouteServiceImpl extends RouteService {
    def incFlag(routeId: String, flagId: String): Try[Unit] = paramsTry(routeId, flagId) {
      routeDao.incFlag(routeId, flagId)
    }
  }
}
