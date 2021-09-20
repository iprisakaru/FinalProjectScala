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

  val commentsRoute: Route = {
    get {
      path(IntNumber) {
        filmId =>
          complete(RouteService.commentsService.getByFilmId(filmId).map(_.toJson))
      }
    } ~ post {
      entity(as[Comment]) {
        entity =>
          complete(RouteService.commentsService.create(entity).map(_.toJson))
      }
    } ~ delete {
      path(IntNumber) {
        id =>
          complete(RouteService.commentsService.deleteById(id).map(_.toJson))
      }
    } ~ put {
      path(IntNumber) {
        id =>
          entity(as[Comment]) {
            entity =>
              complete(RouteService.commentsService.updateById(id, entity).map(_.toJson))
          }
      }
    }
  }
}
