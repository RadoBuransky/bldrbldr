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

object FileController extends Controller {
  def index = Action {
    Ok(views.html.upload())
  }
  
  //upload photo
  def upload = Action(parse.multipartFormData) {
    implicit request => request.body.file("fileInput") match {
      case Some(photo) =>
      	// check if photo is type jpeg or gif
      	if (checkIfPhoto(photo.contentType.get)) {
      	  //get photo into BufferedImage
      	  var orImg = ImageIO.read(photo.ref.file)
      	  
		  //Resize image
		  var rsImg = Scalr.resize(orImg, 150)
		  
		  //set the resize image to file
		  ImageIO.write(rsImg, "jpg", photo.ref.file)
		  
		  //insert photo
		  FileModel.insertFile(photo.ref.file, photo.contentType.orNull)
      	}      	
      	Ok(views.html.upload())
      	
      case None => BadRequest("no photo")
    }
 }

 // check if photo is type jpeg or gif
 def checkIfPhoto(file: String) = {
   file match {
     case "image/jpeg" => true
     case "image/gif" => true
     case _ => false
   }
 }

 //display photo
 def getPhoto(file: String) = Action {/*
   val gridFs = gridFS("photos")
 gridFs.findOne(Map("_id" -> file)) match {
 case Some(f) => SimpleResult(
 ResponseHeader(OK, Map(
 CONTENT_LENGTH -> f.length.toString,
 CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
 DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate)
 )),
 Enumerator.fromStream(f.inputStream)
 )
 case None => NotFound
 }*/
   BadRequest("no photo")
 }
}