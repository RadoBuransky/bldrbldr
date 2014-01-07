package models.data.impl

import models.data.RouteDaoComponent
import scala.concurrent.{ExecutionContext, Future}
import models.data.model.Route
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import play.api.Play.current
import ExecutionContext.Implicits.global
import play.modules.reactivemongo.json.BSONFormats._
import models.data.model.Route
import play.modules.reactivemongo.json.collection.JSONCollection
import common.Utils._

trait MongoRouteDaoComponent extends RouteDaoComponent {
  def routeDao: RouteDao = new MongoRouteDao

  class MongoRouteDao extends RouteDao {
    private val routeColName = "route"
    private implicit val routeFormat = Json.format[Route]

    def findByGymhandle(gymhandle: String): Future[List[Route]] = {
      notEmpty(gymhandle, "gymhandle")

      ReactiveMongoPlugin.db.collection[JSONCollection](routeColName).
        find(Json.obj("gymHandle" -> gymhandle, "enabled" -> true)).
        sort(Json.obj("_id" -> -1)).
        cursor[Route].collect[List](Int.MaxValue, true)
    }

    def incFlag(routeId: String, flagId: String): Unit = {
      notEmpty(routeId, "routeId")
      notEmpty(flagId, "flagId")


    }
  }
}