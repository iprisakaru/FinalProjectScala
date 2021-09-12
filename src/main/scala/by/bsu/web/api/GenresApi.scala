package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.model.repository.Genre
import by.bsu.utils.FilmService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait GenreJsonMapping extends DefaultJsonProtocol with FilmService {

  implicit val genreFormat: RootJsonFormat[Genre] = jsonFormat2(Genre.apply)
}

trait GenresApi extends FilmService with GenreJsonMapping {
  val genresRoute: Route = {
    post {
      complete(getGenresFromApi.map(_.toJson))
    }
  }
}
