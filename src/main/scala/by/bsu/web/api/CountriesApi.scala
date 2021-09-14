package by.bsu.web.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.Application.LOGGER
import by.bsu.model.repository.Country
import by.bsu.utils.RouteService
import by.bsu.utils.RouteService.countriesService
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait CountriesJsonMapping extends DefaultJsonProtocol {
  implicit val countriesFormat: RootJsonFormat[Country] = jsonFormat2(Country.apply)

}

trait CountriesApi extends CountriesJsonMapping {
  val countriesRoute: Route = {
    (path(IntNumber) & get) { id => {
      LOGGER.debug(s"Getting genre with $id id")
      complete(countriesService.getById(id).map(_.get.toJson))
    }
    } ~
      get {
        LOGGER.debug("Getting all genres")
        complete(countriesService.getAll().map(_.toJson))
      } ~
      (path(IntNumber) & put) { id =>
        entity(as[Country]) { entity => {
          LOGGER.debug(s"Updating a new genre with $id id")
          complete(countriesService.update(id, entity).map(_.toJson))
        }
        }
      } ~
      (path(IntNumber) & delete) { id => {
        LOGGER.debug(s"Deleting a genre with $id id")
        complete(countriesService.deleteById(id).map(_.toJson))
      }
      } ~ post {
      entity(as[Country]) { entity => {
        LOGGER.debug(s"Creating a new film with ${entity.id} id")
        complete(countriesService.create(entity).map(_.toJson))
      }
      }
    }
  }

}