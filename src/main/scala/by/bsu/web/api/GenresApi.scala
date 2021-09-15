package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.Application.LOGGER
import by.bsu.model.repository.Genre
import by.bsu.utils.RouteService.genresService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait GenresJsonMapping extends DefaultJsonProtocol {
  implicit val genresFormat: RootJsonFormat[Genre] = jsonFormat2(Genre.apply)

}

trait GenresApi extends GenresJsonMapping {
  val genreRoute: Route = {
    (path(IntNumber) & get) { id => {
      LOGGER.debug(s"Getting genre with $id id")
      complete(genresService.getById(id).map(_.get.toJson))
    }
    } ~
      get {
        LOGGER.debug("Getting all genres")
        complete(genresService.getAll().map(_.toJson))
      } ~
      (path(IntNumber) & put) { id =>
        entity(as[Genre]) { entity => {
          LOGGER.debug(s"Updating a new genre with $id id")
          complete(genresService.update(id, entity).map(_.toJson))
        }
        }
      } ~
      (path(IntNumber) & delete) { id => {
        LOGGER.debug(s"Deleting a genre with $id id")
        complete(genresService.deleteById(id).map(_.toJson))
      }
      } ~ post {
      path("update") {
        updateGenres
      } ~
        entity(as[Genre]) { entity => {
          LOGGER.debug(s"Creating a new film with ${entity.id} id")
          complete(genresService.create(entity).map(_.toJson))
        }
        }
    }
  }

  val updateGenres = {

    (post {
      complete(genresService.getGenresFromApi.map(_.map(_.toJson)))
    })

  }

}
