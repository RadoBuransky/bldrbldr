package com.jugjane.test

import models.domain.model.{Tag, Discipline, Route}
import models.domain.gym.{DemoGradingSystem, Demo}

object TestData {
  val route1: Route = Route("123",
    Demo,
    "/home/rado/123.jpeg",
    DemoGradingSystem.getById("demo3").get,
    Demo.holdColors(0),
    "blaaa",
    Discipline.Bouldering,
    Tag.categories(0) :: Tag.categories(3) :: Nil,
    Tag.flags(1).setCounter(10) :: Tag.flags(0) :: Tag.flags(4).setCounter(0) :: Nil,
    true)
}
