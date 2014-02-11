package com.jugjane.controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import models.domain.{ model => dom }
import models.domain.services._
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.api.mvc.MultipartFormData.FilePart
import scala.Some
import scala.util.{Success, Failure}
import models.domain.model.{Gym, Discipline}
import play.api.Logger
import common.SupportedLang._
import common.SupportedLang

trait RouteController extends Controller with MongoController {
  this: RouteServiceComponent with GymServiceComponent with AuthServiceComponent
    with PhotoServiceComponent =>

  def flag(gymHandle: String, routeId: String, flagId: String) = Action.async {
    routeService.incFlag(routeId, flagId).map { result =>
      Ok
    }
  }
  
  def delete(gymHandle: String, routeId: String) = Action.async { implicit request =>
    adminAction(gymHandle) {
      routeService.getByRouteId(routeId).flatMap { route =>
        val r = for {
          rp <- {
            if (route.fileName.isDefined)
              photoService.remove(route.fileName.get, gymHandle)
            else
              Future()
          }
          dr <- routeService.delete(routeId)
        } yield ()

        r.map { r =>
          Ok
        }
      } recover {
        case _ => NotFound
      }
    }
  }
  
  def get(gymHandle: String, routeId: String) = Action.async { request =>
    routeService.getByRouteId(routeId).map { route =>
      if (!route.enabled)
        NotFound
      else {
        authService.isAdmin(request.cookies, route.gym) match {
          case Success(isAdmin) => {
            val photoUrl = route.fileName.map(photoService.getUrl(_, gymHandle).toString)
            Ok(views.html.route.index(models.ui.Route(route, photoUrl), isAdmin,
              route.gym.address.country))
          }
          case Failure(t) => InternalServerError
        }
      }
    } recover {
      case _ => NotFound
    }
  }
  
  def upload(gymHandle: String): Action[MultipartFormData[TemporaryFile]] =
    Action.async(parse.multipartFormData) { implicit request =>
    doUpload(gymHandle)
  }

  def doUpload(gymHandle: String)(implicit request: Request[MultipartFormData[TemporaryFile]]):
    Future[SimpleResult] = {
    adminAction(gymHandle) {
      val requestFile = request.body.file("photo")
      validate(requestFile, request.body.dataParts) match {
        case Some(validationError) => Promise.successful(BadRequest(validationError)).future
        case None => {
          // Generate random file name
          val newFileName = requestFile.map(_ => photoService.generateFileName())

          gymService.get(gymHandle).map { gym =>
            val store = for {
              uploadPhoto <- {
                if (newFileName.isDefined)
                  photoService.upload(requestFile.get.ref.file, newFileName.get, gymHandle)
                else
                  Future()
              }
              saveToMongo <- saveToMongo(gym, request.body.dataParts, newFileName)
            } yield true

            store.map { result =>
              Ok(views.html.msg("Thank you!", "Go on. Give us another one.",
                new AppLoader.ReversegymController().get(gymHandle, None).url, SupportedLang.defaultLang))
            }
          } match {
            case Success(result) => result
            case Failure(t) => Promise.failed(t).future
          }
        }
      }
    }
  }

  private def adminAction(gymHandle: String)(controller: => Future[SimpleResult])
                         (implicit request: Request[_]): Future[SimpleResult] = {
    val isAdminTry = gymService.get(gymHandle).flatMap { gym =>
      authService.isAdmin(request.cookies, gym)
    }

    isAdminTry match {
      case Failure(t) => Promise.successful(InternalServerError).future
      case Success(isAdmin) if isAdmin => {
        controller
      }
      case _ => Promise.successful(Unauthorized).future
    }
  }
  
  private def saveToMongo(gym: Gym, dataParts: Map[String, Seq[String]],
                          fileName: Option[String]): Future[Unit] = {
    val gradeId = dataParts("grade")(0)
    val coloredHoldsId = dataParts("color")(0)
    val note = dataParts.getOrElse("note", Seq(""))(0)
    val location = dataParts.get("location").map(_(0).trim).flatMap { l =>
      if (l.isEmpty)
        None
      else
        Some(l)
    }

    val categoryIds = dataParts("categories")(0).split(',').filter(c => !c.trim.isEmpty).toList;
    
    dom.Route.create(None, gym, fileName, location, gradeId, coloredHoldsId, note,
      Discipline.Bouldering.toString, categoryIds, Map.empty, true, None).map { route =>
        routeService.save(route)
    } match {
      case Success(result) => result
      case Failure(t) => Promise.failed(t).future
    }
  }

  private def validate(filePart: Option[FilePart[TemporaryFile]],
                       dataParts: Map[String, Seq[String]]): Option[String] = {
    if (filePart.isEmpty) {
      if (dataParts.get("location").isEmpty)
        Some("Either photo or route location in the gym must be provided!")
      else
        None
    }
    else
      validatePhoto(filePart)
  }

  private def validatePhoto(filePart: Option[FilePart[TemporaryFile]]): Option[String] = {
    filePart match {
      case Some(photo) if !checkIfPhoto(photo.contentType.get) => Some("Format not supported!")
      case None => Some("No photo uploaded!")
      case _ => None
    }
  }

  private def checkIfPhoto(file: String) = (file == photoService.getMime)
}