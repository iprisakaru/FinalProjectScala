package by.bsu.web.api.rejections

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, MethodNotAllowed, NotFound, Unauthorized}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.{AuthorizationFailedRejection, MethodRejection, RejectionHandler, ValidationRejection}


trait CustomRejectionHandler {
  implicit def rejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case AuthorizationFailedRejection =>
        val info = s"""{"code":${Unauthorized.intValue},"error":"Authorization","message":"The authorization check failed for you. Access Denied."}""" + "\n"
        complete(HttpResponse(Unauthorized, entity = HttpEntity(ContentTypes.`application/json`, info)))
      }.handle {
      case ValidationRejection(msg, _) =>
        complete(InternalServerError, "That wasn't valid! " + msg)
    }
      .handleAll[MethodRejection] { methodRejections =>
        val names = methodRejections.map(_.supported.name)
        complete(MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!")
      }
      .handleNotFound {
        val info = s"""{"code":${NotFound.intValue},"error":"Not Found","message":"Please check your request again!:)"}""" + "\n"
        complete(HttpResponse(Unauthorized, entity = HttpEntity(ContentTypes.`application/json`, info)))
      }
      .result()
}
