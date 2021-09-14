package by.bsu.web

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.web.api._

import scala.language.postfixOps


trait Routes extends FilmsApi with GenresApi with DirectorsApi with ActorsApi {
  val routes: Route =
    pathPrefix("films") {
      filmRoute
    } ~ pathPrefix("genres") {
      genreRoute
    } ~ pathPrefix("directors") {
      directorRoute
    } ~ pathPrefix("actors") {
      actorsRoute
    } ~ pathPrefix("update-genres"){
      updateGenres
    } ~ pathPrefix("help-create"){
      filmHelpRoute
    }
}
