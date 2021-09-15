package by.bsu.web

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.Application.LOGGER
import by.bsu.web.api._
import by.bsu.web.api.auth.HTTPBasicAuth
import by.bsu.web.api.auth.HTTPBasicAuth.{myAuthenticateBasicAsync, myAuthenticator}

import scala.language.postfixOps


trait Routes extends FilmsApi with GenresApi with DirectorsApi with ActorsApi {
  val routes: Route =
    myAuthenticateBasicAsync("none", myAuthenticator) {
      user =>
        LOGGER.debug(s"Admin $user makes request")
        pathPrefix("films") {
          filmRoute
        } ~ pathPrefix("genres") {
          genreRoute
        } ~ pathPrefix("directors") {
          directorRoute
        } ~ pathPrefix("actors") {
          actorsRoute
        }
    } ~ authenticateOAuth2[]
}
