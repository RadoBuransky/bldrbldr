package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

object Application extends Controller {

	def index = Action {
		Redirect("/editor")
	}
  
  def getCat : String = {
    DB.withConnection { conn =>
      conn.getCatalog() }
  }
}