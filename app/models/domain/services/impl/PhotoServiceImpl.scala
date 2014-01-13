package models.domain.services.impl

import models.domain.services.PhotoServiceComponent
import scala.concurrent.{ExecutionContext, Future}
import common.Utils._
import fly.play.s3.{S3Exception, BucketFile, S3}
import java.net.URL
import java.io.{BufferedInputStream, FileInputStream, File}
import org.imgscalr.Scalr
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import play.api.Logger
import java.util.UUID
import ExecutionContext.Implicits.global

trait PhotoServiceComponentImpl extends PhotoServiceComponent {
  val photoService = new PhotoServiceImpl

  class PhotoServiceImpl extends PhotoService {
    private val bucketName = "jugjane"
    private val UrlTemplate = "http://%s.s3.amazonaws.com/%s"
    private val jpegMime = "image/jpeg"
    private val photoWidth = 800

    def generateFileName(): String = UUID.randomUUID().toString() + ".jpg"

    def upload(file: File, fileName: String): Future[Unit] = {
      notEmpty(fileName, "fileName")

      Logger.info("Resizing...")
      resizeImage(file, photoWidth)

      Logger.info("Uploading to S3...")
      uploadToS3(file.getPath(), fileName)
    }

    def remove(fileName: String): Future[Unit] = {
      notEmpty(fileName, "fileName")
      S3(bucketName).remove(fileName)
    }

    def getUrl(fileName: String): URL = {
      notEmpty(fileName, "fileName")
      new URL(UrlTemplate.format(bucketName, fileName))
    }

    def getMime: String = jpegMime

    private def resizeImage(file: File, width: Int): Unit = {
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

    private def uploadToS3(filePath: String, newFileName: String): Future[Unit] = {
      val fis = new FileInputStream(filePath)
      try {
        val bis = new BufferedInputStream(fis)
        try {
          val byteArray = Stream.continually(bis.read).takeWhile(-1 != _).map(_.toByte).toArray

          val result = S3(bucketName) add BucketFile(newFileName, jpegMime, byteArray)

          result.map { unit =>
            Logger.info("File saved to S3. [" + newFileName + "]")
          } recover {
            case S3Exception(status, code, message, originalXml) => Logger.error("Error: " + message)
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
  }
}
