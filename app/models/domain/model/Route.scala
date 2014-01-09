package models.domain.model

import models.domain.model.Discipline.Discipline
import models.domain.model.Route.RouteId

case class Route(
  id: RouteId,
  gym: Gym,
  fileName: String,
  grade: Grade,
  holdsColor: HoldsColor,
  note: String,
  discipline: Discipline,
  categories: List[CategoryTag],
  flags: List[FlagTag],
  enabled: Boolean)

object Route {
  type RouteId = String
}
