package common

import play.api.i18n.Lang
import scala.language.implicitConversions
import models.JugjaneException
import java.util.Locale

object SupportedLang extends Enumeration {
  type SupportedLang = Value
  val en, sk = Value

  lazy val defaultLang = localeToLang(Locale.ENGLISH)

  def apply(iso: String): SupportedLang = {
    try {
      SupportedLang.withName(iso.toLowerCase)
    }
    catch {
      case ex: Exception => throw new JugjaneException("Unknown language! [" + iso + "]")
    }
  }

  implicit def localeToLang(locale: Locale): Lang = Lang(locale.getLanguage)
}