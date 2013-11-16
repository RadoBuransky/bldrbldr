package models

class Color(val r: Double, val g: Double, val b: Double, val name: String = null) {
  def toWeb = "#" +  floatToHex(r) + floatToHex(g) + floatToHex(b)
  
  private def floatToHex(f: Double) = byteToHex(floatToByte(f))
  
  private def byteToHex(b: Byte) = "%02X" format b
  
  private def floatToByte(f: Double): Byte = {
    val result = (f * 255).toByte
    if (result > 255) {
      255.toByte	
    }
    else {
      result
    }
  }
}

object Color {
  def apply(r: Double, g: Double, b: Double, name: String = null) =
    new Color(r, g, b, name)
  
  val Red = Color(1,0,0,"red")
  val LightRed = Color(1,0.4,0.4,"light red")
  val Orange = Color(1,0.5,0,"orange")
  val Yellow = Color(1,1,0,"yellow")
  val Green = Color(0,0.6,0,"green")
  val LightGreen = Color(0.2,1,0.2,"light green")
  val Blue = Color(0,0,0.6,"blue")
  val LightBlue = Color(0.4,0.4,1,"light blue")
  val Indigo = Color(0.294, 0, 0.51,"indigo")
  val Violet = Color(0.58, 0, 0.827,"violet")
  val White = Color(1,1,1,"white")
  val Black = Color(0,0,0,"black")
  val Brown = Color(0.361, 0.145, 0,"brown")
  val Sand = Color(0.812, 0.812, 0.486,"sand")
}