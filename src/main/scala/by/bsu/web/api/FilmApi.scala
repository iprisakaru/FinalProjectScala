package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.model.dao.{ActorsDAO, FilmsDAO}
import by.bsu.model.repository.{Film, NewFilm}
import by.bsu.utils.FilmService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, _}

import scala.concurrent.ExecutionContext.Implicits.global

trait FilmJsonMapping extends DefaultJsonProtocol with FilmService {
  implicit val newFilmFormat: RootJsonFormat[NewFilm] = jsonFormat12(NewFilm.apply)
  implicit val filmFormat: RootJsonFormat[Film] = jsonFormat10(Film.apply)
}

trait FilmApi extends FilmJsonMapping with FilmService {
  val daoActor = new FilmsDAO(config)
  val filmRoute: Route = {
    (path("create") & post) {
      entity(as[NewFilm]) { customer => {
        
      }
      }
    }
  }
}
