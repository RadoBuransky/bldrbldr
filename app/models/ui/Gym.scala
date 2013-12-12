package models.ui

import models.domain.grade._
import org.joda.time.{DateTime, Days}
import models.domain.gym.{DoubleColoredHolds, SingleColoredHolds}

case class Gym(name: String,
               handle: String,
               url: String,
               address: Address,
               grades: List[Grade],
               routes: Map[String, List[Route]])

case class Route(id: String, color: String, color2: Option[String], note: String, days: Int, categories: List[String])

case class Grade(name: String, id: String, from: Option[String], to: Option[String])

case class Address(street: String, city: String, country: String)

object Route {
  def apply(from: models.data.Route, gym: models.domain.gym.Gym): models.ui.Route = {
    val holdsColor = gym.holdColors.find(hc => hc.name == from.holdsColor).get match {
      case SingleColoredHolds(color) => (color.toWeb, None)
      case DoubleColoredHolds(color1, color2) => (color1.toWeb, Some(color2.toWeb))
    }

    val days = Days.daysBetween(new DateTime(from._id.get.time), DateTime.now()).getDays()
    Route(from._id.get.toString, holdsColor._1, holdsColor._2, from.note, days, from.categories)
  }
}

object Gym {
  def apply(from: models.domain.gym.Gym, grades: List[Grade], routes: Map[String, List[Route]]): models.ui.Gym = {
    Gym(from.name, from.handle, from.url.toString, Address(from.address), grades, routes)
  }
}

object Grade {
  def apply(from: models.domain.grade.Grade): models.ui.Grade = {
    val fromTo = from match {
      case ig: IntervalGrade => (Some(ig.from.name), Some(ig.to.name))
      case _ => (None, None)
    }

    Grade(from.name, from.id, fromTo._1, fromTo._2)
  }
}

object Address {
  def apply(from: models.domain.gym.Address): models.ui.Address = {
    Address(from.street, from.city, from.country.getDisplayCountry())
  }
}

