package models.domain.model

import models.domain.model.Discipline.Discipline
import models.domain.model.Route.RouteId
import org.joda.time.DateTime
import models.data.model
import scala.util.Try
import models.domain.model
import models.domain.model.HoldsColor.ColoredHoldsId
import models.domain.model.Grade.GradeId

case class Route(
  id: Option[RouteId],
  gym: Gym,
  fileName: String,
  grade: Grade,
  holdsColor: HoldsColor,
  note: String,
  discipline: Discipline,
  categories: List[CategoryTag],
  flags: List[FlagTag],
  enabled: Boolean,
  created: Option[DateTime])

object Route {
  type RouteId = String

  def create(id: Option[String], gym: Gym, fileName: String, gradeId: GradeId,
            coloredHoldsId: ColoredHoldsId, note: String,
            disciplineName: String, categoryIds: Iterable[String],
            idsCount: Map[String, Int], enabled: Boolean, created: Option[DateTime]): Try[Route] = {
    gym.gradingSystem.getById(gradeId).flatMap { grade =>
      HoldsColor.getById(gym.holdColors, coloredHoldsId).flatMap { holdsColor =>
        Discipline.getByName(disciplineName).flatMap { discipline =>
          Tag.getCategoriesByIds(categoryIds, gym.categories).flatMap { categories =>
            Tag.getFlagsByIdsCounts(idsCount).map { flags =>
              Route(
                id,
                gym,
                fileName,
                grade,
                holdsColor,
                note,
                discipline,
                categories,
                flags,
                enabled,
                created)
            }
          }
        }
      }
    }
  }
}
