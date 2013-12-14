package models.domain.gym

import models.Color

trait Holds

trait ColoredHolds extends Holds {
  def colors: Set[Color]
  def id: String
}

case class SingleColoredHolds(val color: Color) extends ColoredHolds {
  def colors = Set(color)
  def id = color.id
}

case class DoubleColoredHolds(val color1: Color, val color2: Color) extends ColoredHolds {
  def colors = Set(color1, color2)
  def id = color1.id + "_" + color2.id
}

object ColoredHolds {  
  implicit def fromColor(color: Color): SingleColoredHolds = new SingleColoredHolds(color)
  implicit def fromColors(twoColors: (Color, Color)): DoubleColoredHolds =
    	new DoubleColoredHolds(twoColors._1, twoColors._2)
}