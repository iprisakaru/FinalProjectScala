package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes.{InternalServerError, MethodNotAllowed, NotFound, Unauthorized}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, MethodRejection, RejectionHandler, Route, ValidationRejection}
import by.bsu.model.repository.Comment
import by.bsu.utils.RouteService
import by.bsu.web.api.auth.Auth2.{myAuthenticateOAuthAsync, myAuthenticatorOAuth}
import by.bsu.web.api.rejections.CustomRejectionHandler
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait CommentsJsonMapping extends DefaultJsonProtocol with CustomRejectionHandler{
  implicit val commentsFormat: RootJsonFormat[Comment] = jsonFormat12(Comment.apply)

}

trait CommentsApi extends CommentsJsonMapping {

  def commentsRoute(filmId: Int): Route = Route.seal({
    get {
      complete(RouteService.commentsService.getByFilmId(filmId).map(_.toJson))
    } ~
      myAuthenticateOAuthAsync("none", myAuthenticatorOAuth) {
        id =>
          post {
            entity(as[Comment]) {
              entity =>
                complete(RouteService.commentsService.create(entity.copy(userId = id.toInt, filmId = filmId)).map(_.toJson))
            }
          }
      }
  })

}
