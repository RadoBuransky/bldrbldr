package models.ui

import play.api.data.Form
import play.api.data.Forms._

/**
 * Created by rado on 13/12/13.
 */
package object forms {
  case class CreateBoulder(
    grade: String)

  val createBoulderForm = Form(
    mapping(
      "grade" -> nonEmptyText
    )(CreateBoulder.apply)(CreateBoulder.unapply)
  )
}
