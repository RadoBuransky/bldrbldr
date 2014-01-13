package models.domain.services

import java.net.URL
import scala.concurrent.Future
import java.io.File

trait PhotoServiceComponent {
  def photoService: PhotoService

  trait PhotoService {
    def generateFileName(): String
    def upload(file: File, fileName: String): Future[Unit]
    def remove(fileName: String): Future[Unit]
    def getUrl(fileName: String): URL
    def getMime: String
  }
}