package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.Application.LOGGER
import by.bsu.model.repository.Actor
import by.bsu.utils.RouteService.actorsService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait ActorsJsonMapping extends DefaultJsonProtocol {
  implicit val actorsFormat: RootJsonFormat[Actor] = jsonFormat2(Actor.apply)

}

trait ActorsApi extends ActorsJsonMapping {

  val actorsRoute: Route = {
    (path(IntNumber) & get) { id => {
      LOGGER.debug(s" Getting genre with $id id")
      complete(actorsService.getById(id).map(_.get.toJson))
    }
    } ~
      get {
        LOGGER.debug("Getting all genres")
        complete(actorsService.getAll().map(_.toJson))
      } ~
      (path(IntNumber) & put) { id =>
        entity(as[Actor]) { entity => {
          LOGGER.debug(s"Updating a new genre with $id id")
          complete(actorsService.update(id, entity).map(_.toJson))
        }
        }
      } ~
      (path(IntNumber) & delete) { id => {
        LOGGER.debug(s"Deleting a genre with $id id")
        complete(actorsService.deleteById(id).map(_.toJson))
      }
      } ~ post {
      entity(as[Actor]) { entity => {
        LOGGER.debug(s"Creating a new film with ${entity.id} id")
        complete(actorsService.create(entity).map(_.toJson))
      }
      }

    }
  }

}
