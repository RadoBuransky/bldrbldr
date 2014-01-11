package models.ui

import org.joda.time.{DateTime, Days}
import models.data.model
import models.domain
import models.domain.model._
import models.ui.Color2.WebColor
import models.domain.model.FlagTag
import scala.Some
import models.domain.model.SingleHoldsColor

case class Gym(name: String,
               handle: String,
               url: String,
               address: Address,
               grades: List[Grade],
               routes: Map[String, List[Route]])

case class Color2(name: String, one: WebColor, two: Option[WebColor] = None)

case class Route(d: domain.model.Route, photoUrl: String) {
  val days = Days.daysBetween(d.created, DateTime.now()).getDays()
  val color = Color2(d.holdsColor)
  val categories = d.categories.map { c => c.name }
}

case class Flag(id: String, name: String, count: Int)

case class Grade(name: String, id: String, from: Option[String], to: Option[String]) {
  override def toString(): String = {
    val toS = to match {
      case Some(t) => " - " + t
      case None => ""
    }
    val fromS = from match {
      case Some(f) => " (" + f + toS + ")"
      case None => ""
    }

    name + fromS
  }
}

case class Address(street: String, city: String, country: String)

object Color2 {
  type WebColor = String

  def apply(from: HoldsColor): models.ui.Color2 = {
    from match {
      case (SingleHoldsColor(color)) => Color2(from.id, color.toWeb, None)
      case (DoubleHoldsColor(color1, color2)) => Color2(from.id, color1.toWeb, Some(color2.toWeb))
      case _ => throw new IllegalStateException()
    }
  }
}

//object Route {
//  def apply(from: domain.model.Route): models.ui.Route = {
//    val holdsColor = Color2(gym.holdColors.find(hc => hc.id == from.holdsColor).get)
//    val days = Days.daysBetween(new DateTime(from._id.get.time), DateTime.now()).getDays()
//    Route(from)
//  }
//}

object Flag {
  def apply(from: FlagTag, count: Int): models.ui.Flag = {
      Flag(from.id, from.name, count)
  }
}

object Gym {
  def apply(from: domain.model.Gym, grades: List[Grade], routes: Map[String, List[Route]]): models.ui.Gym = {
    Gym(from.name, from.handle, from.url.toString, Address(from.address), grades, routes)
  }
}

object Grade {
  def apply(from: domain.model.Grade): models.ui.Grade = {
    val fromTo = from match {
      case ig: IntervalGrade => (Some(ig.from.name), Some(ig.to.name))
      case _ => (None, None)
    }

    Grade(from.name, from.id, fromTo._1, fromTo._2)
  }
}

object Address {
  def apply(from: domain.model.Address): models.ui.Address = {
    Address(from.street, from.city, from.country.getDisplayCountry())
  }
}
