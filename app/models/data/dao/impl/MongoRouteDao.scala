package models.data.impl

import scala.concurrent.{ExecutionContext, Future}
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import play.api.Play.current
import ExecutionContext.Implicits.global
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.collection.JSONCollection
import common.Utils._
import models.data.dao.RouteDaoComponent
import models.data.{model => dat}
import models.data.model.{Route}
import reactivemongo.bson.BSONObjectID
import models.JugjaneException

trait MongoRouteDaoComponent extends RouteDaoComponent {
  def routeDao: RouteDao = new MongoRouteDao

  class MongoRouteDao extends RouteDao {
    private val routeColName = "route"
    private implicit val routeFormat = Json.format[dat.Route]

    def getByRouteId(routeId: String): Future[dat.Route] = {
      notEmpty(routeId, "routeId")

      val datRouteOption = ReactiveMongoPlugin.db.collection[JSONCollection](routeColName).
        find(Json.obj("_id" -> BSONObjectID(routeId))).
        cursor[Route].headOption

      datRouteOption.map {
        case None => throw new JugjaneException("Route not found! [" + routeId + "]")
        case Some(r) => r
      }
    }

    def disable(routeId: String): Future[Unit] = {
      notEmpty(routeId, "routeId")

      ReactiveMongoPlugin.db.collection[JSONCollection](routeColName).
        update(Json.obj("_id" -> BSONObjectID(routeId)),
          Json.obj("$set" -> Json.obj("enabled" -> false))).map { lastError =>
      }
    }

    def findByGymhandle(gymHandle: String): Future[List[dat.Route]] = {
      notEmpty(gymHandle, "gymHandle")

      ReactiveMongoPlugin.db.collection[JSONCollection](routeColName).
        find(Json.obj("gymHandle" -> gymHandle, "enabled" -> true)).
        sort(Json.obj("_id" -> -1)).
        cursor[Route].collect[List](Int.MaxValue, true)
    }

    def incFlag(routeId: String, flagId: String): Future[Unit] = {
      notEmpty(routeId, "routeId")
      notEmpty(flagId, "flagId")

      ReactiveMongoPlugin.db.collection[JSONCollection](routeColName).
        update(Json.obj("_id" -> BSONObjectID(routeId)),
          Json.obj("$inc" -> Json.obj(("flags." + flagId) -> 1))).map { lastError =>
      }
    }
  }
}