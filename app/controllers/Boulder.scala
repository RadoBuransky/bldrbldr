package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import models._
import views._
import java.text.SimpleDateFormat
import play.api.libs.iteratee.Enumerator
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import org.imgscalr.Scalr
import scala.concurrent._
import ExecutionContext.Implicits.global
import javax.imageio.ImageWriteParam
import javax.imageio.IIOImage
import fly.play.s3._
import java.io.BufferedInputStream
import java.io.FileInputStream
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import java.io.File
import java.util.UUID
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json._
import models.services.GymService
import models.domain.grade.Discipline._
import models.data.JsonFormats._

object Boulder extends Controller with MongoController {
  private val jpegMime = "image/jpeg"
  private val photoWidth = 800
  
  def upload = Action(parse.multipartFormData) {
    implicit request => {
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
          //uploadPhoto <- uploadPhotoFuture
          saveToMongo <- saveToMongoFuture
        } yield true
        
        store.onSuccess {
          case _ => true
        }
      }
      
      validateResult
    }
  }
  
  private def saveToMongo(dataParts: Map[String, Seq[String]], fileName: String) = {
    val gymhandle = dataParts("gymhandle")(0)
           
    val grade = dataParts("grade")(0).toInt
    val holdcolor = dataParts("holdColors")(0)
    val note = dataParts.getOrElse("note", null) match {
      case ns: Seq[String] => ns(0)
      case null => ""
    }
    
    // New boulder
    val boulder = new models.data.Route(None, gymhandle, fileName, grade, holdcolor, note,
        Bouldering.toString(), true)
    
    db.collection[JSONCollection]("route").insert(boulder)
  }
  
  private def validate(body: MultipartFormData[TemporaryFile]): Result = {
    val gymhandle = body.dataParts("gymhandle")(0)
    val gymsecret = body.dataParts("gymsecret")(0)
    
    if (!GymService.authorize(gymhandle, gymsecret)) {
      Unauthorized
    }
    else {
//	    body.file("file") match {
//	      case Some(photo) => {
//	        // Check photo type
//	        if (!checkIfPhoto(photo.contentType.get)) {
//	          Logger.error("Format not supported! [" + photo.contentType.get + "]")
//	          BadRequest("Format not supported!")
//	        }
//	        else
//	          Ok
//	      }
//	      case None => BadRequest("No photo uploaded!")
//	    }
	    Ok
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
		    val bucket = S3("jugjane")
		    
		    val result = bucket add BucketFile(newFileName, jpegMime, byteArray)
		    
		    result.map {
		      unit => Logger.info("File saved to S3. [" + newFileName + "]")
		    } recover {
		      case S3Exception(status, code, message, originalXml) => Logger.info("Error: " + message)
		    }
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