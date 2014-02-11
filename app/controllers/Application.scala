package com.jugjane.controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import common.SupportedLang

object Application extends Controller {
  def index = Action {
    Ok(views.html.index(SupportedLang.defaultLang))
  }

  def untrail(path: String) = Action {
    MovedPermanently("/" + path)
  }

  def climbing = Action {
    SeeOther("/")
  }
}