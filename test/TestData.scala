package com.jugjane.test

import models.domain.{ model => dom }
import models.data.{ model => dat }
import dom.{Tag, Discipline, Route}
import models.domain.gym.{DemoGradingSystem, Demo}
import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID

object TestData {
  val domRoute1: dom.Route = dom.Route(Some("123"),
    Demo,
    "/home/rado/123.jpeg",
    DemoGradingSystem.getById("demo3").get,
    Demo.holdColors(0),
    "blaaa",
    Discipline.Bouldering,
    Tag.categories(0) :: Tag.categories(3) :: Nil,
    Tag.flags(1).setCounter(10) :: Tag.flags(0) :: Tag.flags(4).setCounter(0) :: Nil,
    true,
    Some(new DateTime(1983, 1, 10, 10, 10)))

  val datRoute1: dat.Route = dat.Route(Some(BSONObjectID.generate),
    "demo",
    "/home/rado/x.jpeg",
    "demo1",
    "white_black",
    "blaaa",
    "Bouldering",
    true,
    List("volumes", "corner"),
    Map("boring" -> 3, "whathow" -> 0))
}
