package by.bsu.web.api.auth

import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.server.Directives.{extractCredentials, onSuccess, provide, reject}
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import by.bsu.utils.RouteService
import com.github.t3hnar.bcrypt._
import org.apache.log4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HTTPBasicAuth  {

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def myAuthenticateBasicAsync[T](realm: String,
                                  authenticate: (String, String) => Future[Option[T]]): Directive1[T] = {
    extractCredentials.flatMap {
      case Some(BasicHttpCredentials(username, password)) =>
        onSuccess(authenticate(username, password)).flatMap {
          case Some(client) => provide(client)
          case None => reject(AuthorizationFailedRejection)
        }
    }
  }

  def myAuthenticator(username: String, password: String): Future[Option[String]] = {
    LOGGER.trace(s"Admin $username trying to insert")
    RouteService.adminsService.getPassword(username).map(pass => {
      if (pass.nonEmpty && password.isBcrypted(pass.get)) {
        LOGGER.trace(s"Admin $username logged in successfully")
        Some(username)
      }
      else {
        LOGGER.trace(s"Admin $username logged in failed")
        None
      }
    })
  }

  case class ErrorResponse(code: Int, `type`: String, message: String)


}
