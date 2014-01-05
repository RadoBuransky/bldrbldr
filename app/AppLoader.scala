import controllers.GymController
import models.data.impl.MongoRouteDaoComponent
import models.services.impl.{AuthServiceComponentImpl, GymServiceComponentImpl}

/**
 * Created by rado on 04/01/14.
 */
package object AppLoader {
  val gymController = new GymController with MongoRouteDaoComponent with GymServiceComponentImpl
    with AuthServiceComponentImpl
}
