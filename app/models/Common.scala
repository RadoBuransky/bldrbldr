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
}