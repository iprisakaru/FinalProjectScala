package by.bsu.web.api

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import akka.http.scaladsl.server.ExceptionHandler

trait ApiErrorHandler {
  implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      extractUri { uri =>
        complete(HttpResponse(NotFound, entity = s" Invalid id: ${e.getMessage}"))
      }
  }
}
