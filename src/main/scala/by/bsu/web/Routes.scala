package by.bsu.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.Application.LOGGER
import by.bsu.utils.RouteService.filmsService
import by.bsu.web.api._
import by.bsu.web.api.auth.HTTPBasicAuth.{myAuthenticateBasicAsync, myAuthenticator}
import spray.json.enrichAny

import scala.concurrent.ExecutionContext.Implicits.global
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
            } ~    pathPrefix( "recommended"){

                complete(filmsService.getRecommend(filmId).map(_.toJson))
            }
        } ~ generalFilmsRoute
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
        }
    )


}


