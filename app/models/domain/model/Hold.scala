package models.domain.model

import models.{JugjaneException, Color}
import scala.language.implicitConversions
import models.domain.model.HoldsColor.ColoredHoldsId
import scala.util.{Failure, Success, Try}

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

  def getById(colors: Seq[HoldsColor], id: ColoredHoldsId): Try[HoldsColor] = {
    colors.find(c => c.id == id) match {
      case Some(c) => Success(c)
      case None => Failure(new JugjaneException("Color doesn't exist! [" + id + "]"))
    }
  }

  implicit def fromColor(color: Color): SingleHoldsColor = new SingleHoldsColor(color)
  implicit def fromColors(twoColors: (Color, Color)): DoubleHoldsColor =
    	new DoubleHoldsColor(twoColors._1, twoColors._2)
}