package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route}
import by.bsu.Application.LOGGER
import by.bsu.model.repository.{Film, NewFilmWithFields, NewFilmWithFieldsId, NewFilmWithId}
import by.bsu.utils.RouteService.filmsService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait FilmJsonMapping extends DefaultJsonProtocol {
  implicit val film1Format: RootJsonFormat[NewFilmWithId] = jsonFormat14(NewFilmWithId.apply)
  implicit val film2Format: RootJsonFormat[Film] = jsonFormat10(Film.apply)
  implicit val film3Format: RootJsonFormat[NewFilmWithFields] = jsonFormat14(NewFilmWithFields.apply)
  implicit val film4Format: RootJsonFormat[NewFilmWithFieldsId] = jsonFormat14(NewFilmWithFieldsId.apply)
}

trait FilmsApi extends FilmJsonMapping with CommentsApi {
  val filmRoute: Route = {
    delete {
      (path(IntNumber)) { id => {
        LOGGER.debug(s"Deleting a film with $id id")
        complete(filmsService.deleteById(id).map(_.toJson))
      }
      }
    } ~
      get {
        path("private") {
          LOGGER.debug("Getting all private films")
          complete(filmsService.getAllPrivate)
        } ~ {
          LOGGER.debug("Getting all public films")
          complete(filmsService.getAllPublic.map(_.toJson))
        } ~
          (path(IntNumber)) { id => {
            LOGGER.debug(s"Getting films with $id id")
            complete(filmsService.getById(id).map(_.toJson))
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
      post {

        extractRequestContext { ctx: RequestContext =>
          fileUpload("csv") {
            case (metadata, byteSource) =>
              LOGGER.debug(s"File ${metadata.fileName} with format ${metadata.contentType}")
              complete(filmsService.parseCSVtoFilm(byteSource, ctx))

          }
        } ~
        path("help") {
          filmHelpRoute
        } ~
          entity(as[NewFilmWithId]) { entity => {
            LOGGER.debug(s"Creating a new film with ${entity.id} id")
            complete(filmsService.createWithoutFilling(entity).map(_.toJson))
          }
          }
      } ~
      put {
        path("public" / IntNumber) {
          id =>
            LOGGER.debug(s"Making film $id id public")
            complete(filmsService.makePublic(id).map(_.toJson))
        }
      }

  }

  val filmHelpRoute: Route = {

    post {
      entity(as[NewFilmWithFields]) { customer => {
        complete(filmsService.createFilmWithFilling(customer).map(_.toJson))
      }
      }
    }

  }

  val generalFilmsRoute: Route = {
    get {
      path("directors") {
        parameter("name") {
          name =>
            complete(filmsService.getFullFilmsByDirector(name).map(_.toJson))
        }
      } ~
        parameter("name", "releaseDate", "directorName") { (name, date, directorName) =>
          LOGGER.debug(s"Searching with params: name $name, date: $date, director name: $directorName")
          complete(filmsService.getFullByDirectorNameDate(directorName, name, date).map(_.toJson))
        } ~ parameter("name", "releaseDate") {
        (name, date) =>
          complete(filmsService.getFullByNameDate(name, date))
      } ~ parameter("name", "directorName") {
        (name, directorName) =>
          complete(filmsService.getFullByDirectorName(directorName, name).map(_.toJson))
      } ~ parameter("releaseDate", "directorName") {
        (date, directorName) =>
          complete(filmsService.getFullByDirectorDate(directorName, date).map(_.toJson))
      } ~ parameter("releaseDate") {
        (date) =>
          complete(filmsService.getFullByDate(date).map(_.toJson))
      } ~ parameter("name") {
        (name) =>
          LOGGER.debug(s"Getting film by name $name")
          complete(filmsService.getFullByName(name).map(_.toJson))
      } ~ {
        complete(filmsService.getAllPublic.map(_.toJson))
      }
    }
  }


}
