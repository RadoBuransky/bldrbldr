package controllers

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import org.imgscalr.Scalr
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import models.contract.JsonMapper
import models.domain.grade.Discipline.Bouldering
import models.services.GymService
import models.services.PhotoService
import play.api.{Routes, Logger}
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc._
import play.api.mvc.MultipartFormData.FilePart
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID
import models.services.AuthService
import models.data.JsonFormats._
import play.modules.reactivemongo.json.BSONFormats._
import models.domain.route.Tag
import play.api.mvc.MultipartFormData.FilePart
import scala.Some
import play.modules.reactivemongo.json.collection.JSONCollection
import play.mvc.Http
import models.data.model.Route
import models.data.model.Route

object RouteController extends Controller with MongoController {
  private val jpegMime = "image/jpeg"
  private val photoWidth = 800

  def flag(gymHandle: String, routeId: String, flagId: String) = Action.async {
    incFlag(routeId, flagId) map { lastError => Ok }
  }
  
  def delete(gymHandle: String, routeId: String) = Action.async { request =>
    // Get gym by handle
    val gym = GymService.get(gymHandle)
    
    if (!AuthService.isAdmin(request.cookies, gym)) {
      future { Unauthorized }
    }
    else {    
      getBoulder(routeId).map {	route =>
        route match {
          case None => NotFound
          case Some(route) => {
            // Delete file from S3
            val removePhotoFuture = logFuture("removePhoto") {
              PhotoService.remove(route.fileName)
            }
            val disableRouteFuture = logFuture("disableRoute") {
              disableRoute(routeId)
            }

            for {
              rp <- removePhotoFuture
              dr <- disableRouteFuture
            } yield true

            Ok
          }
        }
      }
    }
  }
  
  def get(gymHandle: String, routeId: String) = Action.async { request =>
    getBoulder(routeId).map {	route =>
      route match {
        case Some(route) => {
          if (!route.enabled)
            BadRequest("Route is disabled.")
          else {
            // Get gym by handle
            val gym = GymService.get(gymHandle)

            val grade = models.ui.Grade(gym.gradingSystem.findById(route.gradeId).get)
            val flags = Tag.flags.map(flag => {
              val count = route.flags.getOrElse(flag.id, 0)
              models.ui.Flag(flag, count)
            })
            val isAdmin = AuthService.isAdmin(request.cookies, gym)
            Ok(views.html.route.index(models.ui.Route(route, gym), gym.name, gym.handle, grade, flags,
              PhotoService.getUrl(route.fileName).toString, isAdmin))
          }
        }
        case None => NotFound
      }
    }
  }
  
  def upload(gymHandle: String) = Action(parse.multipartFormData) { request => {
	    // Get gym by handle
	    val gym = GymService.get(gymHandle)
	    
	    if (!AuthService.isAdmin(request.cookies, gym)) {
	      Unauthorized
	    }
	    else {
	      val validateResult = validate(request.body)
	      
	      if (validateResult == Ok) {
		      // Generate random UUID file name 
		      val fileName = UUID.randomUUID().toString() + ".jpg"
	      
	        val uploadPhotoFuture = logFuture("uploadPhoto") {
	      		uploadPhoto(request.body.file("photo"), fileName)
	        }
	        val saveToMongoFuture = logFuture("saveToMongo") {
	          saveToMongo(gymHandle, request.body.dataParts, fileName)
	        }
	        
	        val store = for {
	          uploadPhoto <- uploadPhotoFuture
	          saveToMongo <- saveToMongoFuture
	        } yield true
	        
	        store.onSuccess {
	          case _ => true
	        }

          Ok(views.html.msg("Thank you!", "Go on. Give us another one.",
            new AppLoader.ReversegymController().get(gymHandle, None).url))
	      }
        else
	        validateResult
	    }
  	}
  }

  private def incFlag(routeId: String, flagId: String) = {
    db.collection[JSONCollection]("route").
      update(Json.obj("_id" -> BSONObjectID(routeId)),
        Json.obj("$inc" -> Json.obj(("flags." + flagId) -> 1)))
  }
  
  private def disableRoute(routeId: String) = {
    db.collection[JSONCollection]("route").
    	update(Json.obj("_id" -> BSONObjectID(routeId)),
    	    Json.obj("$set" -> Json.obj("enabled" -> false)))    
  }
  
  private def getBoulder(routeId: String): Future[Option[Route]] = {
    db.collection[JSONCollection]("route").
    	find(Json.obj("_id" -> BSONObjectID(routeId))).
    	cursor[Route].headOption
  }
  
  private def saveToMongo(gymHandle: String, dataParts: Map[String, Seq[String]], fileName: String) = {
    val gradeId = dataParts("grade")(0)
    val holdsColor = dataParts("color")(0)
    val note = dataParts.getOrElse("note", null) match {
      case ns: Seq[String] => ns(0)
      case null => ""
    }

    val categories = dataParts("categories")(0).split(',').filter(c => !c.trim.isEmpty).toList;
    
    // New boulder
    val boulder = new Route(None, gymHandle, fileName, gradeId, holdsColor, note,
        Bouldering.toString(), true, categories, Map.empty)
    
    db.collection[JSONCollection]("route").insert(boulder)
  }
  
  private def validate(body: MultipartFormData[TemporaryFile]): Result = {
	    body.file("photo") match {
	      case Some(photo) => {
	        // Check photo type
	        if (!checkIfPhoto(photo.contentType.get)) {
	          Logger.error("Format not supported! [" + photo.contentType.get + "]")
	          BadRequest("Format not supported!")
	        }
	        else
	          Ok
	      }
	      case None => BadRequest("No photo uploaded!")
	    }
  }

  private def uploadPhoto(file: Option[FilePart[TemporaryFile]], fileName: String) = {
    val photo = file.get
    try {
	    Logger.info("Resizing...")	
	    resizeImage(photo.ref.file, photoWidth)
	
	    Logger.info("Uploading to S3...")
			uploadToS3(photo.ref.file.getPath(), fileName)	    
	  }
    catch {
	    case ex: Throwable => {
	      Logger.error("Cannot process uploaded photo!", ex)
	      BadRequest("Cannot process uploaded photo!")
	    }
	  }
  }
  
  private def uploadToS3(filePath: String, newFileName: String) = {
    val fis = new FileInputStream(filePath)
    try {
	    val bis = new BufferedInputStream(fis)
	    try {
		    val byteArray = Stream.continually(bis.read).takeWhile(-1 != _).map(_.toByte).toArray
		    PhotoService.upload(newFileName, jpegMime, byteArray)
	    }
	    finally {
	      bis.close()
	    }
    }
    finally {
      fis.close()
    }
  }
  
  private def resizeImage(file: File, width: Int) = {
    // Read and resize image
    val rsImg = Scalr.resize(ImageIO.read(file), width)

    val jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
    try {
      // Set quality
      val param = jpgWriter.getDefaultWriteParam();
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(0.5f);

      // Write to temporary file
      file.delete()
      val outputStream = ImageIO.createImageOutputStream(file)
      try {
        jpgWriter.setOutput(outputStream);
        jpgWriter.write(null, new IIOImage(rsImg, null, null), param);
      } finally {
        outputStream.close();
      }
    }
    finally {
      jpgWriter.dispose();
    }    
  }

  private def checkIfPhoto(file: String): Boolean = {
    file match {
      case `jpegMime` => true
      case _ => false
    }
  }
  
  private def logFuture[T](msg: String)(body: =>T): Future[T] = {
    val f = Future[T](body)
    f.onFailure {
      case ex: Exception => {
        Logger.error("Future error! [" + msg + "]", ex)
      }
    }
    f
  }
}