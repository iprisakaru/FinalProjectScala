package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.model.repository.Comment
import by.bsu.utils.RouteService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait CommentsJsonMapping extends DefaultJsonProtocol {
  implicit val commentsFormat: RootJsonFormat[Comment] = jsonFormat12(Comment.apply)

}

trait CommentsApi extends CommentsJsonMapping {

  def commentsRoute(filmId: Int): Route = {
    get {
      complete(RouteService.commentsService.getByFilmId(filmId).map(_.toJson))
    } ~ post {
      entity(as[Comment]) {
        entity =>
          complete(RouteService.commentsService.create(entity.copy(filmId = filmId)).map(_.toJson))
      }
    } ~ delete {
      complete(RouteService.commentsService.deleteById(filmId).map(_.toJson))

    } ~ put {
      entity(as[Comment]) {
        entity =>
          complete(RouteService.commentsService.updateById(filmId, entity).map(_.toJson))
      }
    }
  }
}
