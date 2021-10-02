package by.bsu.web

import akka.http.scaladsl.model.StatusCodes.Unauthorized
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, RejectionHandler, Route}
import by.bsu.Application.LOGGER
import by.bsu.web.api._
import by.bsu.web.api.auth.Auth2.{myAuthenticateOAuthAsync, myAuthenticatorOAuth}
import by.bsu.web.api.auth.HTTPBasicAuth.{myAuthenticateBasicAsync, myAuthenticator}

import scala.language.postfixOps

trait Routes extends FilmsApi with AuthApi with GenresApi
  with DirectorsApi with ActorsApi with CommentsApi {


  val routes =
    Route.seal(
      pathPrefix("films") {
        pathPrefix(IntNumber) {
          filmId =>
            pathPrefix("comments") {
              LOGGER.debug(s"Trying to write a comment to film $filmId id")
              commentsRoute(filmId)
            }
        }
      } ~
        myAuthenticateBasicAsync("none", myAuthenticator) {
          user =>
            pathPrefix("films") {
              filmRoute
            } ~ pathPrefix("genres") {
              genreRoute
            } ~ pathPrefix("directors") {
              directorRoute
            } ~ pathPrefix("actors") {
              actorsRoute
            } ~ pathPrefix("job") {
              periodicRequest
            }
        } ~
        generalFilmsRoute
    )


}


