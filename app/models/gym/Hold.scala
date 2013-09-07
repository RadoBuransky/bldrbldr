package models.gym

import models.Color

trait Holds
trait ColoredHolds extends Holds {
  def colors: Set[Color]
}
case class SingleColoredHolds(val color: Color) extends ColoredHolds {
  def colors = Set(color)
  implicit def fromColor(color: Color): SingleColoredHolds = new SingleColoredHolds(color)
}
case class DoubleColoredHolds(val color1: Color, val color2: Color) extends ColoredHolds {
  def colors = Set(color1, color2)
}

object ColoredHolds {  
  implicit def fromColor(color: Color): SingleColoredHolds = new SingleColoredHolds(color)
}