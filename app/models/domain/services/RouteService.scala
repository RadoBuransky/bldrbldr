package models.domain.services

import scala.util.Try
import models.domain.{ model => dom }
import scala.concurrent.Future

trait RouteServiceComponent {
  def routeService: RouteService

  trait RouteService {
    def getByRouteId(routeId: dom.Route.RouteId): Future[dom.Route]
    def delete(routeId: dom.Route.RouteId): Future[Unit]
    def incFlag(routeId: dom.Route.RouteId, flagId: dom.Tag.TagId): Future[Unit]
  }
}
