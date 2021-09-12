package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.Application.LOGGER
import by.bsu.model.repository.{Film, NewFilm}
import by.bsu.utils.FilmService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait FilmJsonMapping extends DefaultJsonProtocol with FilmService {
  implicit val newFilmFormat: RootJsonFormat[NewFilm] = jsonFormat12(NewFilm.apply)
  implicit val filmFormat: RootJsonFormat[Film] = jsonFormat10(Film.apply)
}

trait FilmApi extends FilmJsonMapping with FilmService {
  val filmRoute: Route = {
    (post) {
      entity(as[NewFilm]) { customer => {
        LOGGER.debug(s"${customer.toString}")
        complete(createFilm(customer)._6.map(_.toOption.get).map(_.toJson))
      }
      }
    } ~
      (path(IntNumber) & get) { id =>
        complete(getFilmById(id).map(_.toJson))
      } ~
      (get) {
        complete(getAllFilms)
      } ~ (path(IntNumber) & delete) { id =>
      complete(deleteById(id).map(_.toJson))
    } ~ (put) {
      complete(updateFilmsPerDay().map(_.toJson))
    }

  }
}
