package models.domain.services.impl

import models.domain.services.PhotoServiceComponent
import scala.concurrent.Future
import common.Utils._
import fly.play.s3.S3
import java.net.URL

trait PhotoServiceComponentImpl extends PhotoServiceComponent {
  val photoService = new PhotoServiceImpl

  class PhotoServiceImpl extends PhotoService {
    private val bucketName = "jugjane"
    private val UrlTemplate = "http://%s.s3.amazonaws.com/%s"

    def remove(fileName: String): Future[Unit] = {
      notEmpty(fileName, "fileName")
      S3(bucketName).remove(fileName)
    }

    def getUrl(fileName: String): URL = {
      notEmpty(fileName, "fileName")
      new URL(UrlTemplate.format(bucketName, fileName))
    }
  }
}
