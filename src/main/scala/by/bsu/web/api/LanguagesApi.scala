package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.Application.LOGGER
import by.bsu.model.repository.Language
import by.bsu.utils.RouteService.languagesService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait LanguagesJsonMapping extends DefaultJsonProtocol {
  implicit val languagesFormat: RootJsonFormat[Language] = jsonFormat2(Language.apply)

}

trait LanguagesApi extends LanguagesJsonMapping {
  val languagesRoute: Route = {

    (pathPrefix(IntNumber) & get) { id => {

      LOGGER.debug(s"Getting genre with $id id")
      complete(languagesService.getById(id).map(_.get.toJson))
    }
    } ~ get {

      LOGGER.debug("Getting all genres")
      complete(languagesService.getAll().map(_.toJson))

    } ~ (pathPrefix(IntNumber) & put) { id =>
      entity(as[Language]) { entity => {

        LOGGER.debug(s"Updating a new genre with $id id")
        complete(languagesService.update(id, entity).map(_.toJson))
      }
      }
    } ~ (pathPrefix(IntNumber) & delete) { id => {

      LOGGER.debug(s"Deleting a genre with $id id")
      complete(languagesService.deleteById(id).map(_.toJson))
    }
    } ~ post {
      entity(as[Language]) { entity => {
        LOGGER.debug(s"Creating a new film with ${entity.id} id")
        complete(languagesService.create(entity).map(_.toJson))
      }
      }

    }
  }

}