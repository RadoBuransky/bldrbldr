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
import models.domain.model.Discipline

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
          rp <- photoService.remove(route.fileName)
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
            val photoUrl = photoService.getUrl(route.fileName).toString
            Ok(views.html.route.index(models.ui.Route(route, photoUrl), isAdmin))
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
      validate(requestFile) match {
        case Some(validationError) => Promise.successful(BadRequest(validationError)).future
        case None => {
          // Generate random file name
          val newFileName = photoService.generateFileName()

          val store = for {
            uploadPhoto <- photoService.upload(requestFile.get.ref.file, newFileName)
            saveToMongo <- saveToMongo(gymHandle, request.body.dataParts, newFileName)
          } yield true

          store.map { result =>
            Ok(views.html.msg("Thank you!", "Go on. Give us another one.",
              new AppLoader.ReversegymController().get(gymHandle, None).url))
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
  
  private def saveToMongo(gymHandle: String, dataParts: Map[String, Seq[String]],
                          fileName: String): Future[Unit] = {
    val gradeId = dataParts("grade")(0)
    val coloredHoldsId = dataParts("color")(0)
    val note = dataParts.getOrElse("note", Seq(""))(0)

    val categoryIds = dataParts("categories")(0).split(',').filter(c => !c.trim.isEmpty).toList;
    
    gymService.get(gymHandle).flatMap { gym =>
      dom.Route.create(None, gym, fileName, gradeId, coloredHoldsId, note,
        Discipline.Bouldering.toString, categoryIds, Map.empty, true, None).map { route =>
        routeService.save(route)
      }
    } match {
      case Success(result) => result
      case Failure(t) => Promise.failed(t).future
    }
  }
  
  private def validate(filePart: Option[FilePart[TemporaryFile]]): Option[String] = {
    filePart match {
      case Some(photo) if !checkIfPhoto(photo.contentType.get) => Some("Format not supported!")
      case None => Some("No photo uploaded!")
      case _ => None
    }
  }

  private def checkIfPhoto(file: String) = (file == photoService.getMime)
}