package models.domain.model

import models.Color
import scala.language.implicitConversions
import models.domain.model.HoldsColor.ColoredHoldsId

trait Holds

trait HoldsColor extends Holds {
  def colors: Seq[Color]
  def id: ColoredHoldsId
}

case class SingleHoldsColor(color: Color) extends HoldsColor {
  val colors = Seq(color)
  val id = color.id
}

case class DoubleHoldsColor(color1: Color, color2: Color) extends HoldsColor {
  val colors = Seq(color1, color2)
  val id = color1.id + "_" + color2.id
}

object HoldsColor {
  type ColoredHoldsId = String

  implicit def fromColor(color: Color): SingleHoldsColor = new SingleHoldsColor(color)
  implicit def fromColors(twoColors: (Color, Color)): DoubleHoldsColor =
    	new DoubleHoldsColor(twoColors._1, twoColors._2)
}