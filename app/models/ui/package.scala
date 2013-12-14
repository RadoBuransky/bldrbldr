package models

import models.domain.gym.{DoubleColoredHolds, SingleColoredHolds}
import org.joda.time.{DateTime, Days}
import models.ui.Route
import models.domain.grade.IntervalGrade

/**
 * Created by rado on 13/12/13.
 */
package object ui {
  case class Gym(name: String,
                 handle: String,
                 url: String,
                 address: Address,
                 grades: List[Grade],
                 routes: Map[String, List[Route]])

  type WebColor = String

  case class Color2(name: String, one: WebColor, two: Option[WebColor] = None)

  case class Route(id: String, color: Color2, note: String, days: Int, categories: List[String])

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
    def apply(from: models.domain.gym.ColoredHolds): models.ui.Color2 = {
      from match {
        case (SingleColoredHolds(color)) => Color2(from.id, color.toWeb)
        case (DoubleColoredHolds(color1, color2)) => Color2(from.id, color1.toWeb, Some(color2.toWeb))
        case _ => throw new IllegalStateException()
      }
    }
  }

  object Route {
    def apply(from: models.data.Route, gym: models.domain.gym.Gym):
      models.ui.Route = {
      val holdsColor = Color2(gym.holdColors.find(hc => hc.id == from.holdsColor).get)
      val days = Days.daysBetween(new DateTime(from._id.get.time), DateTime.now()).getDays()
      holdsColor.two.isEmpty
      Route(from._id.get.stringify, holdsColor, from.note, days, from.categories)
    }
  }

  object Flag {
    def apply(from: models.domain.route.FlagTag, count: Int): models.ui.Flag = {
        Flag(from.id, from.name, count)
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
}
