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
import models.domain.{model => dom}
import models.data.{model => dat}
import models.data.model.{Mapping, Route}
import reactivemongo.bson.BSONObjectID
import models.JugjaneException

trait MongoRouteDaoComponent extends RouteDaoComponent {
  def routeDao: RouteDao = new MongoRouteDao

  class MongoRouteDao extends RouteDao {
    private val routeColName = "route"
    private implicit val routeFormat = Json.format[dat.Route]

    def getByRouteId(routeId: dom.Route.RouteId): Future[dom.Route] = {
      notEmpty(routeId, "routeId")

      val datRouteOption = ReactiveMongoPlugin.db.collection[JSONCollection](routeColName).
        find(Json.obj("_id" -> BSONObjectID(routeId))).
        cursor[Route].headOption

      datRouteOption.map {
        case None => throw new JugjaneException("Route not found! [" + routeId + "]")
        case Some(r) => Mapping.toDomain(r)
      }
    }

    def findByGymhandle(gymhandle: String): Future[List[dat.Route]] = {
      notEmpty(gymhandle, "gymhandle")

      ReactiveMongoPlugin.db.collection[JSONCollection](routeColName).
        find(Json.obj("gymHandle" -> gymhandle, "enabled" -> true)).
        sort(Json.obj("_id" -> -1)).
        cursor[Route].collect[List](Int.MaxValue, true)
    }

    def incFlag(routeId: dom.Route.RouteId, flagId: dom.Tag.TagId): Unit = {
      notEmpty(routeId, "routeId")
      notEmpty(flagId, "flagId")
    }
  }
}