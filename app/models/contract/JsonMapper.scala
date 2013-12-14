package models.contract

import models.data.Route
import play.api.libs.json.JsObject
import org.joda.time.Days
import org.joda.time.DateTime
import play.api.libs.json.Json
import models.domain.gym._
import models.services.PhotoService
import models.domain.grade._
import models.domain.route.Tag
import models.domain.gym.SingleColoredHolds
import models.domain.gym.DoubleColoredHolds
import play.api.libs.json.JsObject
import models.data.Route
import scala.Some
import models.domain.gym.Address

object JsonMapper {
  def tagsToJson[T <: Tag](tags: Traversable[T]): List[JsObject] = {
    tags.map(tag => tagToJson(tag)).toList
  }

  def tagToJson[T <: Tag](tag: T): JsObject = {
    Json.obj("id" -> tag.id,
      "name" -> tag.name)
  }

  def routeToJson(gym: models.domain.gym.Gym, route: Route): JsObject = {
    val days = Days.daysBetween(new DateTime(route._id.get.time), DateTime.now()).getDays()
    Json.obj("id" -> route._id.get.stringify,
      "holdcolor" -> holdColorByName(gym, route.holdsColor),
      "note" -> route.note,
      "days" -> days,
      "photo" -> PhotoService.getUrl(route.fileName).toString,
      "grade" -> gradeToJson(gym.gradingSystem.findById(route.gradeId)),
      "categories" -> routeCategoriesToJson(gym, route.categories),
      "flags" -> flagsToJson(route.flags))
  }
  
  def holdColorByName(gym: models.domain.gym.Gym, holdcolor: String): JsObject = {
    gym.holdColors.find(h => h.name == holdcolor) match {
      case Some(h) => holdColorToJson(h)
      case None => Json.obj()
    }
  }
  
  def holdColorToJson(holdColor: ColoredHolds): JsObject = {
    holdColor match {
  		case (SingleColoredHolds(color)) =>
  		  Json.obj("name" -> holdColor.name, "color" -> color.toWeb)
		  case (DoubleColoredHolds(color1, color2)) =>
  		  Json.obj("name" -> holdColor.name, "color" -> color1.toWeb, "color2" -> color2.toWeb)
		  case _ => Json.obj()
    }
  }
  
  def gymToJson(gym: models.domain.gym.Gym): JsObject = {
    Json.obj("name" -> gym.name,
      "handle" -> gym.handle,
      "url" -> gym.url.toString(),
      "address" -> addressToJson(gym.address))
  }  
  
  def gradeToJson(grade: Option[Grade]): JsObject = {
    grade match {
      case Some(g) => {
    		val name = g match {
          case ex: ExactGrade => ex.value
          case _ => g.name
        }

        val color = g match {
          case cg: SingleColorGrade => Json.obj("color" -> cg.color.toWeb)
          case _ => Json.obj()
        }

        val interval = g match {
          case ig: IntervalGrade => {
            Json.obj("from" -> gradeToJson(Some(ig.from)),
              "to" -> gradeToJson(Some(ig.to)))
          }
          case _ => Json.obj()
        }

        Json.obj("name" -> name) ++ color ++ interval
      }
      case None => Json.obj()
    }
  }

  private def flagsToJson(flags: Map[String, Int]) = {
    Tag.flags.map { f =>
      Json.obj("id" -> f.id,
        "name" -> f.name,
        "count" -> {
          flags.get(f.id) match {
            case Some(c) => c
            case _ => 0
          }
        })
    }
  }

  private def addressToJson(address: Address): JsObject = {
    Json.obj("street" -> address.street,
      "city" -> address.city,
      "country" -> address.country.getDisplayCountry())
  }

  private def routeCategoriesToJson(gym: models.domain.gym.Gym, categories: List[String]) = {
    val allCategories = gym.categories ++  Tag.categories;

    categories.map(c => allCategories.find(ct => ct.id == c) match {
      case Some(categoryTag) => Some(categoryTag.name)
      case _ => None
    }
    ).flatten
  }
}