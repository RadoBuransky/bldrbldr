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

object Boulder extends Controller {
  
  def upload = Action(parse.multipartFormData) {
    implicit request => {
      val result = validate(request.body)
      
      if (result == Ok) {
        val uploadPhotoFuture = future {
      		uploadPhoto(request.body.file("file"))
        }
        val saveToMongoFuture = future {
          saveToMongo(request.body.dataParts)
        }
        
        val store = for {
          uploadPhoto <- uploadPhotoFuture
          saveToMongo <- saveToMongoFuture
        } yield true
        
        store.onSuccess {
          case _ => true
        }
      }
      
      result
    }
  }
  
  private def saveToMongo(dataParts: Map[String, Seq[String]]) = {
    
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

  private def uploadPhoto(file: Option[FilePart[TemporaryFile]]) = {
    val photo = file.get
	  try {
	    Logger.info("Resizing...")
	
	    // Read and resize image
	    val rsImg = Scalr.resize(ImageIO.read(photo.ref.file), 800)
	
	    val jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
	    try {
	      // Set quality
	      val param = jpgWriter.getDefaultWriteParam();
	      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	      param.setCompressionQuality(0.5f);
	
	      // Write to temporary file
	      photo.ref.file.delete()
	      val outputStream = ImageIO.createImageOutputStream(photo.ref.file)
	      try {
	        jpgWriter.setOutput(outputStream);
	        jpgWriter.write(null, new IIOImage(rsImg, null, null), param);
	      } finally {
	        outputStream.close();
	      }
	    } finally {
	      jpgWriter.dispose();
	    }
	
	    Logger.info("Uploading to S3...")
	
	    val bis = new BufferedInputStream(new FileInputStream(photo.ref.file.getPath()))
	    val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
	    val bucket = S3("jugjane")
	    val result = bucket add BucketFile("xxx.jpg", photo.contentType.get, byteArray)
	    result
	      .map { unit =>
	        Logger.info("Saved the file")
	      }
	      .recover {
	        case S3Exception(status, code, message, originalXml) => Logger.info("Error: " + message)
	      }
	  } catch {
	    case ex: Throwable => {
	      Logger.error("Cannot process uploaded photo!", ex)
	      BadRequest("Cannot process uploaded photo!")
	    }
	  }
  }

  private def checkIfPhoto(file: String): Boolean = {
    file match {
      case "image/jpeg" => true
      case _ => false
    }
  }
}