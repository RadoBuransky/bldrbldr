package models.domain.model.gym

import models.domain.model.{HoldsColor, Gym}
import java.net.URL
import models.domain.model
import models.Color
import java.util.Locale
import models.domain.model.grade.Uiaa

object K2 extends Gym {
  import HoldsColor._

  val name = "K2"
  val url = new URL("http://www.lezeckastena.sk/")
  val handle = "k2"
  val gradingSystem = Uiaa
  val disciplines = Set(model.Discipline.Climbing)
  val holdColors: List[HoldsColor] =
    List(Color.Red, Color.LightRed, Color.Orange, Color.Yellow,
      Color.Green, Color.LightGreen, Color.Blue, Color.LightBlue,
      Color.Indigo, Color.Violet, Color.White, Color.Black, Color.Brown, Color.Sand,
      (Color.Yellow, Color.Blue), (Color.White, Color.Black), (Color.Sand, Color.Black),
      (Color.Red, Color.Black), (Color.Green, Color.Black))
  val categories = Nil
  val address = model.Address("Stará Ivanská cesta 1/B", "Bratislava", Locale.forLanguageTag("sk-sk"))
}
