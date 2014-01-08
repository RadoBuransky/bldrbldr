package models.services

import scala.util.Try

trait RouteServiceComponent {
  def routeService: RouteService

  trait RouteService {
    def delete(routeId: String): Try[Unit]
    def incFlag(routeId: String, flagId: String): Try[Unit]
  }
}
