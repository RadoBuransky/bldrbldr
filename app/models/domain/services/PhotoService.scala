package models.domain.services

import java.net.URL
import scala.concurrent.Future
import java.io.File
import common.Environment
import models.domain.model.Gym

trait PhotoServiceComponent {
  def photoService: PhotoService

  trait PhotoService {
    def generateFileName(): String
    def upload(file: File, fileName: String, gymHandle: String)(implicit environment: Environment): Future[Unit]
    def remove(fileName: String, gymHandle: String)(implicit environment: Environment): Future[Unit]
    def getUrl(fileName: String, gymHandle: String)(implicit environment: Environment): URL
    def getMime: String
  }
}