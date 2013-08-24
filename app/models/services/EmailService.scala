package models.services

import com.typesafe.plugin._
import play.api.libs.concurrent.Akka
import play.api.Play.current

object EmailService {
  def send(): Unit = {    
    Akka.future {
      val mail = use[MailerPlugin].email;
      mail.setSubject("JugJane - Test");
      mail.addRecipient("radoburansky@gmail.com");
      mail.addFrom("Rado Buransky <rado@buransky.com>");
      mail.send("text")
    }
  }
}