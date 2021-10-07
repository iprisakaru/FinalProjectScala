package by.bsu.web.api.auth

import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Directives.{extractCredentials, onSuccess, provide, reject}
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import by.bsu.utils.RouteService

import scala.concurrent.Future

object OAuth2 {

  def myAuthenticateOAuthAsync[T](realm: String,
                                  authenticate: (String) => Future[Option[T]]): Directive1[T] = {
    extractCredentials.flatMap {
      case Some(OAuth2BearerToken(token)) =>
        onSuccess(authenticate(token)).flatMap {
          case Some(client) => provide(client)
          case None => reject(AuthorizationFailedRejection)
        }
    }
  }

  def myAuthenticatorOAuth(accessToken: String): Future[Option[String]] = {
    RouteService.authService.authByToken(accessToken)
  }

}
