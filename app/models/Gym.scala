package models

import org.joda.time.DateTime
import play.data.validation.Constraints.Required

case class Gym (
    gymname: String,
    email: String,
    link: String,
    created: Option[DateTime],
    validated: Option[Boolean],
    disabled: Option[Boolean],
    secret: Option[String]
)

object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  implicit val gymFormat = Json.format[Gym]
}