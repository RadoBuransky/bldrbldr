package com.jugjane.controllers

import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def untrail(path: String) = Action {
    MovedPermanently("/" + path)
  }

  def climbing = Action {
    SeeOther("/")
  }
}