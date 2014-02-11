package com.jugjane.common

import play.api.i18n.Lang
import play.api.i18n

object Messages {
  object Controllers {
    object Route {
      def thankYou(implicit lang: Lang) = i18n.Messages("controllers.route.doUpload.thankYou")
      def goOn(implicit lang: Lang) = i18n.Messages("controllers.route.doUpload.goOn")
    }
  }

  object Models {
    def flag(id: String)(implicit lang: Lang) =  i18n.Messages("models.flags." + id)
    def category(id: String)(implicit lang: Lang) =  i18n.Messages("models.categories." + id)
  }
}
