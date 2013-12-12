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
import models.data.Route
import models.domain.grade.Discipline.Bouldering
import models.services.GymService
import models.services.PhotoService
import play.api.Logger
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.Result
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID
import models.services.AuthService
import models.data.JsonFormats._
import play.modules.reactivemongo.json.BSONFormats._

object Boulder extends Controller with MongoController {
  private val jpegMime = "image/jpeg"
  private val photoWidth = 800

  def flag(gymHandle: String, routeId: String, flagId: String) = Action {
    Async {
      incFlag(routeId, flagId) map { lastError =>
        Ok
      }
    }
  }

  private def incFlag(routeId: String, flagId: String) = {
    db.collection[JSONCollection]("route").
      update(Json.obj("_id" -> BSONObjectID(routeId)),
      Json.obj("$inc" -> Json.obj(("flags." + flagId) -> 1)))
  }
  
  def delete(gymHandle: String, routeId: String) = Action { request =>
    // Get gym by handle
    val gym = GymService.get(gymHandle)
    
    if (!AuthService.isAdmin(request.cookies, gym)) {
      Unauthorized
    }
    else {    
	  	Async {	  	  	    
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
  }
  
  def get(gymHandle: String, routeId: String) = Action { request =>
    Async {
      getBoulder(routeId).map {	route =>
        route match {
          case Some(route) => {
            if (!route.enabled)
              BadRequest("Route is disabled.")
            else {
              // Get gym by handle
              val gym = GymService.get(gymHandle)

              Ok(Json.obj("route" -> JsonMapper.routeToJson(gym, route),
                "gym" -> JsonMapper.gymToJson(gym),
                "isAdmin" -> AuthService.isAdmin(request.cookies, gym)))
            }
          }
          case None => NotFound
        }
      }
    }
  }
  
  def upload = Action(parse.multipartFormData) { request => {
	    val gymHandle = request.body.dataParts("gymName")(0)
	    
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
	      		uploadPhoto(request.body.file("file"), fileName)
	        }
	        val saveToMongoFuture = logFuture("saveToMongo") {
	          saveToMongo(request.body.dataParts, fileName)
	        }
	        
	        val store = for {
	          uploadPhoto <- uploadPhotoFuture
	          saveToMongo <- saveToMongoFuture
	        } yield true
	        
	        store.onSuccess {
	          case _ => true
	        }
	      }
	      
	      validateResult
	    }
  	}
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
  
  private def saveToMongo(dataParts: Map[String, Seq[String]], fileName: String) = {
    val gymName = dataParts("gymName")(0)
           
    val gradeId = dataParts("gradeId")(0)
    val holdsColor = dataParts("holdsColor")(0)
    val note = dataParts.getOrElse("note", null) match {
      case ns: Seq[String] => ns(0)
      case null => ""
    }

    val categories = dataParts("tags")(0).split(',').toList;
    
    // New boulder
    val boulder = new models.data.Route(None, gymName, fileName, gradeId, holdsColor, note,
        Bouldering.toString(), true, categories, Map.empty)
    
    db.collection[JSONCollection]("route").insert(boulder)
  }
  
  private def validate(body: MultipartFormData[TemporaryFile]): Result = {
	    body.file("file") match {
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
		    val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
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