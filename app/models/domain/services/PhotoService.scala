package models.domain.services

import fly.play.s3._
import java.net.URL
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global

object PhotoService {
  private val BucketName = "jugjane"
  private val UrlTemplate = "http://%s.s3.amazonaws.com/%s"
  
  def upload(fileName: String, mimeType: String, data: Array[Byte]) = {
  	val bucket = S3(BucketName)
		    
    val result = bucket add BucketFile(fileName, mimeType, data)
    
    result.map {
      unit => Logger.info("File saved to S3. [" + fileName + "]")
    } recover {
      case S3Exception(status, code, message, originalXml) => Logger.error("Error: " + message)
    }  
  }
  
  def getUrl(fileName: String): URL = {
    new URL(UrlTemplate.format(BucketName, fileName))
  }
  
  def remove(fileName: String) = {
  	val bucket = S3(BucketName)  	
  	bucket.remove(fileName)    
  }
}