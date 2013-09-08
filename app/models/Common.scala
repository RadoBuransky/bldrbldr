package models

class Color(val r: Float, val g: Float, val b: Float, val name: String = null) {
  def toWeb = "#" +  floatToHex(r) + floatToHex(g) + floatToHex(b)
  
  private def floatToHex(f: Float) = byteToHex(floatToByte(f))
  
  private def byteToHex(b: Byte) = "%02X" format b
  
  private def floatToByte(f: Float): Byte = {
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
  def apply(r: Float, g: Float, b: Float, name: String = null) =
    new Color(r, g, b, name)
  
  val Red = Color(1,0,0,"red")
  val Green = Color(0,1,0,"green")
  val Blue = Color(0,0,1,"blue")
  val Yellow = Color(1,1,0,"yellow")
  val Purple = Color(0.7f,1,0.5f,"purple")
  val White = Color(1,1,1,"white")
}