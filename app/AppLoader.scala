import controllers.GymController
import models.data.impl.MongoRouteDaoComponent
import models.domain.services.impl.{AuthServiceComponentImpl, GymServiceComponentImpl}
import play.api.Play
import play.api.Play.current

/**
 * Created by rado on 04/01/14.
 */
package object AppLoader {
  val gymController = new GymController with MongoRouteDaoComponent with GymServiceComponentImpl
    with AuthServiceComponentImpl with PlayConfiguration

  trait PlayConfiguration {
    val configuration = Play.configuration
  }
}
