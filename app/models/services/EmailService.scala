package models.services

import com.typesafe.plugin._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.Play
import play.api.Logger
import scala.io.Source
import models.JugJaneException

object EmailTemplate extends Enumeration {
  type EmailTemplate = Value
  val NewGym = Value
}

object EmailService {
  def newGym(to: String): Unit = {
    send(to, "activation", EmailTemplate.NewGym)
  }

  import EmailTemplate._

  private def send(to: String, subject: String, emailTemplate: EmailTemplate): Unit = {
    Akka.future {
      try {
        val mail = use[MailerPlugin].email;
        mail.setSubject("Jug Jane: " + subject);
        mail.addRecipient(to);
        mail.addFrom("Rado Buransky <rado@buransky.com>");

        val body = getContents(emailTemplate)
        
        mail.send(body)
        Logger.info("Email sent. [" + to + "," + emailTemplate + "]")
        
      } catch {
        case e: Exception =>
          Logger.error("Cannot send email! [" + to + "," + emailTemplate + "]", e)
          throw e
      }
    }
  }

  private def getContents(emailTemplate: EmailTemplate): String = {
    val resourceName = getResourceName(emailTemplate)
    val stream = getClass.getClassLoader.getResourceAsStream(resourceName)
    if (stream == null)
      throw new JugJaneException("Cannot load resource! [" + resourceName + "]");

    Source.fromInputStream(stream).getLines().mkString("\n");
  }

  private def getResourceName(emailTemplate: EmailTemplate): String = {
    emailTemplate match {
      case EmailTemplate.NewGym => "emails/newgym.txt"
    }
  }
}