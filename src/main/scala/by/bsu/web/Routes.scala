package by.bsu.web

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.web.api._

import scala.language.postfixOps

trait Routes extends FilmApi with GenresApi with ApiErrorHandler {
  val routes: Route =
     pathPrefix( "film" / "help") {
      filmHelpRoute
    }~pathPrefix( "film") {
       filmRoute
     } ~ pathPrefix("genres") {
      genresRoute
    }

}
