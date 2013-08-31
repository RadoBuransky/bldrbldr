package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import models._
import views._
import java.text.SimpleDateFormat
import play.api.libs.iteratee.Enumerator
import java.awt.image.BufferedImage
import javax.imageio.ImageIO;
import org.imgscalr.Scalr
import scala.concurrent._
import ExecutionContext.Implicits.global

object Boulder extends Controller {
  def upload = Action(parse.multipartFormData) {
    implicit request =>
      request.body.file("file") match {
        case Some(photo) => {
          // Check photo type
          if (checkIfPhoto(photo.contentType.get)) {
            Async {
              	future {
              	  	// Read and resize image
		            val rsImg = Scalr.resize(ImageIO.read(photo.ref.file), 150)
		
		            // Write file
		            ImageIO.write(rsImg, "jpg", photo.ref.file)
		
		            //insert photo
		            //FileModel.insertFile(photo.ref.file, photo.contentType.orNull)
		            
		            Ok		            
              	}
            }
          }
          else
            BadRequest("Format not supported!")
        }

        case None => BadRequest("No photo uploaded!")
      }
  }

  private def checkIfPhoto(file: String): Boolean = {
    file match {
      case "image/jpeg" => true
      case _ => false
    }
  }
}