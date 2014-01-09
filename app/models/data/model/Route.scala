package models.data.model

import reactivemongo.bson.BSONObjectID
import models.domain.{model => dom}
import models.data.{model => dat}

case class Route (
  _id: Option[BSONObjectID],
  gymHandle: String,
  fileName: String,
  gradeId: String,
  holdsColor: String,
  note: String,
  discipline: String,
  enabled: Boolean,
  categories: List[String],
  flags: Map[String, Int])