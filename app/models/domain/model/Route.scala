package models.domain.model

import Gym.GymHandle
import models.domain.model.Discipline.Discipline
import models.domain.model.Grade.GradeId
import models.domain.model.Route.RouteId

case class Route(
  id: RouteId,
  gymHandle: GymHandle,
  fileName: String,
  gradeId: GradeId,
  holdsColor: HoldsColor,
  note: String,
  discipline: Discipline,
  categories: List[CategoryTag],
  flags: List[FlagTag],
  enabled: Boolean)

object Route {
  type RouteId = String
}
