package models

case class Area (
    email: String,
    areaname: String,
    link: String,
    areatype: String // 0 - indoor, 1 - outdoor
)

object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  implicit val areaFormat = Json.format[Area]
}