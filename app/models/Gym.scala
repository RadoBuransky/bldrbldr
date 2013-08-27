package models

import org.joda.time.DateTime
import play.data.validation.Constraints.Required

case class Gym (
  gymname: String = null,
  email: String = null,
  link: String = null,
  created: Option[DateTime] = null,
  validated: Option[Boolean] = null,
  disabled: Option[Boolean] = null,
  approved: Option[Boolean] = null,
  secret: Option[String] = null
)

object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  implicit val gymFormat = Json.format[Gym]
}