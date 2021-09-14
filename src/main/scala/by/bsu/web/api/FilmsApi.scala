package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.Application.LOGGER
import by.bsu.model.repository.{Film, NewFilmWithFields, NewFilmWithId}
import by.bsu.utils.RouteService.filmsService
import by.bsu.web.api.auth.HTTPBasicAuth
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait FilmJsonMapping extends DefaultJsonProtocol {
  implicit val film1Format: RootJsonFormat[NewFilmWithId] = jsonFormat14(NewFilmWithId.apply)
  implicit val film2Format: RootJsonFormat[Film] = jsonFormat10(Film.apply)
  implicit val film3Format: RootJsonFormat[NewFilmWithFields] = jsonFormat12(NewFilmWithFields.apply)
}

trait FilmsApi extends FilmJsonMapping {
  val filmRoute: Route = {
    authenticateBasicAsync("authorisation", HTTPBasicAuth.myAdminsPassAuthenticator) {
      user =>
        (path(IntNumber) & get) { id => {
          LOGGER.debug(s"Getting films with $id id")
          complete(filmsService.getById(id).map(_.toJson))
        }
        } ~
          get {
            LOGGER.debug("Getting all films")
            complete(filmsService.getAll().map(_.toJson))
          } ~
          post {
            entity(as[NewFilmWithId]) { entity => {
              LOGGER.debug(s"Creating a new film with ${entity.id} id")
              complete(filmsService.createWithoutFilling(entity).map(_.toJson))
            }
            }
          } ~
          (path(IntNumber) & put) { id =>
            entity(as[Film]) { entity => {
              LOGGER.debug(s"Updating a new film with $id id")
              complete(filmsService.updateById(id, entity).map(_.toJson))
            }
            }
          } ~
          (path(IntNumber) & delete) { id => {
            LOGGER.debug(s"Deleting a film with $id id")
            complete(filmsService.deleteById(id).map(_.toJson))
          }
          }
    }
  }

  val filmHelpRoute: Route = {
    authenticateBasicAsync(
      "authorisation", HTTPBasicAuth.myAdminsPassAuthenticator) {
      adminName =>
        LOGGER.debug(s"Admin $adminName created film with a help of another API.")
        post {
          entity(as[NewFilmWithFields]) { customer => {
            complete(filmsService.createFilmWithFilling(customer).map(_.toJson))
          }
          }
        }
    }
  }


}
