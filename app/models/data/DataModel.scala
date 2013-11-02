package models.data

import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.modules.reactivemongo.json.BSONFormats._
import models.domain.grade.Discipline._

case class Gym (
  _id: Option[BSONObjectID] = None,
  gymName: String,
  email: String,
  link: String,
  handle: Option[String] = None,
  created: Option[DateTime] = None,
  validated: Option[Boolean] = None,
  disabled: Option[Boolean] = None,
  approved: Option[Boolean] = None,
  secret: Option[String] = None
)

case class Route (
    _id: Option[BSONObjectID],
    gymName: String,
    fileName: String,
    gradeId: String,
    holdsColor: String,
    note: String,
    discipline: String,
    enabled: Boolean
)

object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  implicit val gymFormat = Json.format[Gym]
  implicit val routeFormat = Json.format[Route]
}