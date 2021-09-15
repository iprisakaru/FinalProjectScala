package by.bsu.web.api.auth

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, HttpChallenges}
import akka.http.scaladsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1}
import akka.http.scaladsl.server.Directives.{extractCredentials, onSuccess, provide, reject}
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.ActorMaterializer
import by.bsu.utils.RouteService
import com.github.t3hnar.bcrypt._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HTTPBasicAuth {

  def myAuthenticateBasicAsync[T](realm: String,
                                  authenticate: (String, String) => Future[Option[T]]): Directive1[T] = {
    def challenge = HttpChallenges.basic(realm)

    extractCredentials.flatMap {
      case Some(BasicHttpCredentials(username, password)) =>
        onSuccess(authenticate(username, password)).flatMap {
          case Some(client) => provide(client)
          case None => reject(AuthenticationFailedRejection(CredentialsRejected, challenge))
        }
      case _ => reject(AuthenticationFailedRejection(CredentialsMissing, challenge))
    }
  }

  def myAuthenticator(username: String, password: String): Future[Option[String]] = {
    RouteService.adminsService.getPassword(username).map(pass => {
      if (password.isBcrypted(pass)) Some(username)
      else None
    })
  }

}
