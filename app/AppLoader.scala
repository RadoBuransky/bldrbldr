import controllers.GymController
import models.data.impl.MongoRouteDaoComponent

/**
 * Created by rado on 04/01/14.
 */
package object AppLoader {
  val gymController = new GymController with MongoRouteDaoComponent
}
