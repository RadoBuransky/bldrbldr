package controllers

import play.api._
import play.api.mvc._
import java.net.URLDecoder

object Area extends Controller {  
  def newarea = Action {
    Ok(views.html.area.newarea())
  }
  
  def newareapost = Action {
    request => Redirect(routes.Application.msg("Thank you!",
        "New area has been created. You may now start creating boulders.",
        routes.Application.index.url, false))
  }
}