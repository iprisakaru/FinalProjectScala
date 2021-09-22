package by.bsu.web

import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, RejectionHandler, Route}
import by.bsu.Application.LOGGER
import by.bsu.web.api._
import by.bsu.web.api.auth.HTTPBasicAuth.{ErrorResponse, myAuthenticateBasicAsync, myAuthenticator}
import by.bsu.web.api.auth.JsonHelper

import scala.language.postfixOps


trait Routes extends FilmsApi with GenresApi with DirectorsApi with ActorsApi with CommentsApi with JsonHelper {

  implicit def rejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case AuthorizationFailedRejection =>
        LOGGER.debug("there")
        val errorResponse = write(ErrorResponse(BadRequest.intValue, "Authorization", "The authorization check failed for you. Access Denied."))
        complete(HttpResponse(BadRequest, entity = HttpEntity(ContentTypes.`application/json`, errorResponse)))
      }
      .result()

  val routes =
    Route.seal(
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
          } ~ pathPrefix("periodicity") {
            periodicRequest
          }
      }~ pathPrefix("films") {

        pathPrefix(IntNumber) {
          filmId =>
            pathPrefix("comments") {
              LOGGER.debug(s"Trying to write a comment to film $filmId id")
              commentsRoute(filmId)
            }
        } ~
          generalFilmsRoute
      }
    )



}
