package models.data.model

import play.modules.reactivemongo.json.BSONFormats._

object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  implicit val routeFormat = Json.format[Route]
}