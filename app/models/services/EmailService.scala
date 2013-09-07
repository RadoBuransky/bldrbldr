package models.services

import com.typesafe.plugin._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.Play
import play.api.Logger
import scala.io.Source
import models.JugJaneException
import controllers.routes
import java.net.URL
import models.Gym
import controllers.GymCtrl

object EmailTemplate extends Enumeration {
  type EmailTemplate = Value
  val NewGym, NewGymNotification = Value
}
  
private object Tag extends Enumeration {
  type Tag = Value
  val GymActivationLink, GymSecret, GymApprovalLink = Value 
}

object EmailService {  
  def newGym(to: String, gym: Gym): Unit = {
    send(to, "Registration at JUGJANE", EmailTemplate.NewGym, getContextTagValues(gym))
  }
  
  def newGymNotif(gym: Gym): Unit = {
    send("radoburansky@gmail.com", "New gym: " + gym.gymname, EmailTemplate.NewGymNotification,
        getContextTagValues(gym))
  }

  import EmailTemplate._
  import Tag._
  
  private def getContextTagValues(gym: Gym): Map[Tag, String] = {
    Map(Tag.GymSecret -> gym.secret.get)
  }
  
  private def send(to: String, subject: String, emailTemplate: EmailTemplate,
      contextTagValues: Map[Tag, String]): Unit = {
    Akka.future {
      try {
        val mail = use[MailerPlugin].email;
        mail.setSubject(subject);
        mail.addRecipient(to);
        mail.addFrom("Rado Buransky <rado@buransky.com>");
        
        //Logger.debug(replaceTags(getContents(emailTemplate), contextTagValues))
        mail.send(replaceTags(getContents(emailTemplate), contextTagValues))
        Logger.info("Email sent. [" + to + "," + emailTemplate + "]")
        
      } catch {
        case e: Exception =>
          Logger.error("Cannot send email! [" + to + "," + emailTemplate + "]", e)
          throw e
      }
    }
  }
  
  private def replaceTags(body: String, contextTagValues: Map[Tag, String]): String = {
    if (contextTagValues == null) {
      body
    }
    else {
      var result = body    
  	  getTagValues(contextTagValues) foreach {
        case (tag, value) => {
          result = result.replaceAllLiterally(putTagIntoBrackets(tag), value)
        }
      }    
      result
    }
  }
  
  private def putTagIntoBrackets(tagValue: String): String = {
    "{" + tagValue + "}"
  }
  
  private def getTagValues(contextTagValues: Map[Tag, String]): Set[(String, String)] = {
    Tag.values.map(tag => (tag.toString, getTagValue(tag, contextTagValues)))
  }
  
  private def getTagValue(tag: Tag, contextTagValues: Map[Tag, String]): String = {
    contextTagValues.get(tag) match {
      case (Some(value)) => value
      case _ => {
        tag match {
          case (Tag.GymActivationLink) => {
            getAbsoluteUrl("/#" + routes.GymCtrl.validate(getTagValue(Tag.GymSecret, contextTagValues)).url)
          }
          case (Tag.GymApprovalLink) => {
            getAbsoluteUrl("/#" + routes.GymCtrl.approve(getTagValue(Tag.GymSecret, contextTagValues)).url)
          }
        }
      }
    }
  }
  
  private def getAbsoluteUrl(url: String): String = {
    Play.configuration.getString("application.baseUrl").get + url
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
      case EmailTemplate.NewGymNotification => "emails/newgymnotif.txt"
    }
  }
}