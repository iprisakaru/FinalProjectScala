package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import by.bsu.Application.LOGGER
import by.bsu.model.repository.Director
import by.bsu.utils.RouteService
import by.bsu.utils.RouteService.directorsService
import by.bsu.web.api.auth.HTTPBasicAuth
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

case class NoneDirector(value: Option[Director])

trait DirectorsJsonMapping extends DefaultJsonProtocol {
  implicit val directorsFormat: RootJsonFormat[Director] = jsonFormat2(Director.apply)
}


trait DirectorsApi extends DirectorsJsonMapping {
  val directorRoute: Route = {
    authenticateBasicAsync("authorisation", HTTPBasicAuth.myAdminsPassAuthenticator) {
      user =>
        (path(IntNumber) & get) { id => {
          LOGGER.debug(s"Getting films with $id id")
          complete(directorsService.getById(id).map(_.toJson))
        }
        } ~
          get {
            LOGGER.debug("Getting all films")
            complete(directorsService.getAll().map(_.toJson))
          } ~
          (path(IntNumber) & put) { id =>
            entity(as[Director]) { entity => {
              LOGGER.debug(s"Updating a new film with $id id")
              complete(directorsService.update(id, entity).map(_.toJson))
            }
            }
          } ~
          (path(IntNumber) & delete) { id => {
            LOGGER.debug(s"Deleting a film with $id id")
            complete(directorsService.deleteById(id).map(_.toJson))
          }
          } ~ post {
          entity(as[Director]) { entity => {
            LOGGER.debug(s"Creating a new film with ${entity.id} id")
            complete(directorsService.create(entity).map(_.toJson))
          }
          }
        }
    }
  }


}