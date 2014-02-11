package models.domain.model.gym

import models.domain.model.{CategoryTag, HoldsColor, Gym}
import java.net.URL
import models.domain.model
import models.Color
import java.util.Locale
import models.domain.grade.Hueco

object Vertigo extends Gym {
  import HoldsColor._

  val name = "Vertigo"
  val url = new URL("http://www.lezeckecentrum.sk/")
  val handle = "vertigo"
  val gradingSystem = Hueco
  val disciplines = Set(model.Discipline.Climbing)
  val holdColors: List[HoldsColor] =
    List(Color.Red, Color.LightRed, Color.Orange, Color.Yellow,
      Color.Green, Color.LightGreen, Color.Blue, Color.LightBlue,
      Color.Indigo, Color.Violet, Color.White, Color.Black, Color.Brown, Color.Sand,
      (Color.Yellow, Color.Blue), (Color.White, Color.Black), (Color.Sand, Color.Black),
      (Color.Red, Color.Black), (Color.Green, Color.Black))
  val categories = List(CategoryTag("x"))
  val address = model.Address("Trenčianska 47", "Bratislava", Locale.forLanguageTag("sk-sk"))
}
