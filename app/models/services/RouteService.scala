package models.services

import scala.util.Try

trait RouteServiceComponent {
  def routeService: RouteService

  trait RouteService {
    def incFlag(routeId: String, flagId: String): Try[Unit]
  }
}
