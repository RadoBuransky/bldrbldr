import com.jugjane.controllers.{RouteController, GymController}
import models.data.impl.MongoRouteDaoComponent
import models.domain.services.impl.{PhotoServiceComponentImpl, RouteServiceComponentImpl, AuthServiceComponentImpl, GymServiceComponentImpl}
import play.api.Play
import play.api.Play.current

package object AppLoader {
  val gymController = new GymController with MongoRouteDaoComponent with GymServiceComponentImpl
    with AuthServiceComponentImpl with PlayConfiguration with RouteServiceComponentImpl
    with PhotoServiceComponentImpl

  val routeController = new RouteController with RouteServiceComponentImpl with GymServiceComponentImpl
    with MongoRouteDaoComponent with AuthServiceComponentImpl with PlayConfiguration
    with PhotoServiceComponentImpl

  trait PlayConfiguration {
    val configuration = Play.configuration
  }
}
