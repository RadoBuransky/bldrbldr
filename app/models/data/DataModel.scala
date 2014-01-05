package models.data

import play.modules.reactivemongo.json.BSONFormats._
import models.data.model.{Route}

object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  implicit val routeFormat = Json.format[Route]
}