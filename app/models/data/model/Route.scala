package models.data.model

import reactivemongo.bson.BSONObjectID

case class Route(
  _id: Option[BSONObjectID],
  gymHandle: String,
  fileName: Option[String],
  location: Option[String],
  gradeId: String,
  holdsColor: String,
  note: String,
  discipline: String,
  enabled: Boolean,
  categories: List[String],
  flags: Map[String, Int])