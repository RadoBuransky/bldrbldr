package models.domain.grade

case class HuecoGrade(val value: String) extends ExactGrade {
  val name = value
  val id = value
}

object Hueco extends GradingSystem[HuecoGrade]("Hueco", Set(Discipline.Bouldering), 
    HuecoGrade("V0") ::
    HuecoGrade("V0+") ::
    HuecoGrade("V1") ::
    HuecoGrade("V2") ::
    HuecoGrade("V3") ::
    HuecoGrade("V4") ::
    HuecoGrade("V5") ::
    HuecoGrade("V6") ::
    HuecoGrade("V7") ::
    HuecoGrade("V8") ::
    HuecoGrade("V9") ::
    HuecoGrade("V10") ::
    HuecoGrade("V11") ::
    HuecoGrade("V12") ::
    HuecoGrade("V13") ::
    HuecoGrade("V14") ::
    HuecoGrade("V15") ::
    HuecoGrade("V16") ::
    Nil) {
  def V0 = grades(0)
  def V1 = grades(2)
  def V2 = grades(3)
  def V3 = grades(4)
  def V4 = grades(5)
  def V5 = grades(6)
  def V6 = grades(7)
  def V7 = grades(8)
  def V8 = grades(9)
  def V9 = grades(10)
  def V10 = grades(11)
}