package models.contract

import models.data.Route
import play.api.libs.json.JsObject
import org.joda.time.Days
import org.joda.time.DateTime
import play.api.libs.json.Json
import models.domain.gym._
import models.services.PhotoService
import models.domain.grade.Grade

object JsonMapper {
  def routeToJson(gym: models.domain.gym.Gym, route: Route): JsObject = {
    val days = Days.daysBetween(new DateTime(route._id.get.time), DateTime.now()).getDays()
    Json.obj("id" -> route._id.get.stringify,
        "holdcolor" -> holdColorByName(gym, route.holdsColor),
        "note" -> route.note,
        "days" -> days,
        "photo" -> PhotoService.getUrl(route.fileName).toString,
        "grade" -> gradeToJson(gym.gradingSystem.findById(route.gradeId)))
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
			    	"url" -> gym.url.toString())
  }  
  
  def gradeToJson(grade: Option[Grade]): JsObject = {
    grade match {
      case Some(g) => {
    		Json.obj("name" -> Grade.getName(g),
    		    "color" -> (Grade.getColor(g) match {
          		case Some(c) => c.toWeb
        		}))        
      }
      case None => Json.obj()
    }
  }
}