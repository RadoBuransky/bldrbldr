package models.data.model

import models.domain.{model => dom}
import models.data.{model => dat}

object Mapping {
  def toDomain(route: dat.Route): dom.Route = {
    dom.Route(route._id.get.stringify,
      route.gymHandle,
      route.fileName,
      route.gradeId,
      HoldsColor(route.holdsColor)
    )
  }
}
