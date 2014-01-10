package models.domain.services.impl

import models.domain.services.PhotoServiceComponent
import scala.concurrent.Future
import common.Utils._
import fly.play.s3.S3

trait PhotoServiceComponentImpl extends PhotoServiceComponent {
  val photoService = new PhotoServiceImpl

  class PhotoServiceImpl extends PhotoService {
    private val bucketName = "jugjane"

    def remove(fileName: String): Future[Unit] = {
      notEmpty(fileName, "fileName")
      S3(bucketName).remove(fileName)
    }
  }
}
