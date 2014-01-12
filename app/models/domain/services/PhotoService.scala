package models.domain.services

import fly.play.s3._
import java.net.URL
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PhotoService {
  private val BucketName = "jugjane"
  
  def upload(fileName: String, mimeType: String, data: Array[Byte]) = {
  	val bucket = S3(BucketName)
		    
    val result = bucket add BucketFile(fileName, mimeType, data)
    
    result.map {
      unit => Logger.info("File saved to S3. [" + fileName + "]")
    } recover {
      case S3Exception(status, code, message, originalXml) => Logger.error("Error: " + message)
    }  
  }
}

trait PhotoServiceComponent {
  def photoService: PhotoService

  trait PhotoService {
    def remove(fileName: String): Future[Unit]
    def getUrl(fileName: String): URL
  }
}