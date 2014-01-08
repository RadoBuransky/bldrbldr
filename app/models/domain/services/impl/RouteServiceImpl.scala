package models.services.impl

import models.services.RouteServiceComponent
import scala.util.Try
import common.Utils._
import models.data.dao.RouteDaoComponent

trait RouteServiceComponentImpl extends RouteServiceComponent {
  this: RouteDaoComponent =>

  val routeService = new RouteServiceImpl

  class RouteServiceImpl extends RouteService {
    def delete(routeId: String): Try[Unit] = paramsTry(routeId) {
      notEmpty(routeId, "routeId")


    }

    def incFlag(routeId: String, flagId: String): Try[Unit] = paramsTry(routeId, flagId) {
      routeDao.incFlag(routeId, flagId)
    }
  }
}
