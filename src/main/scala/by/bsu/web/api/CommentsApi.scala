package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.model.repository.{Comment, CommentEntrance}
import by.bsu.utils.RouteService
import by.bsu.web.api.auth.OAuth2.{myAuthenticateOAuthAsync, myAuthenticatorOAuth}
import by.bsu.web.api.rejections.CustomRejectionHandler
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait CommentsJsonMapping extends DefaultJsonProtocol with CustomRejectionHandler {
  implicit val commentsFormat: RootJsonFormat[Comment] = jsonFormat12(Comment.apply)
  implicit val commentsEntranceFormat: RootJsonFormat[CommentEntrance] = jsonFormat9(CommentEntrance.apply)

}

trait CommentsApi extends CommentsJsonMapping {

  def commentsRoute(filmId: Int): Route = Route.seal({
    get {
      complete(RouteService.commentsService.getByFilmId(filmId).map(_.toJson))
    } ~
      myAuthenticateOAuthAsync("none", myAuthenticatorOAuth) {
        id =>
          post {
            entity(as[CommentEntrance]) {
              entity =>
                complete(RouteService.commentsService.create(Comment(None, entity.header,entity.description,entity.rating,entity.recommended, id.toInt, filmId, entity.recommendedFilm1, entity.recommendedFilm2, entity.recommendedFilm3, entity.recommendedFilm4, entity.recommendedFilm5 )))
            }
          }
      }
  })

}
