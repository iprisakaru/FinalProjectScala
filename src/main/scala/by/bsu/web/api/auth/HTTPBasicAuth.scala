package by.bsu.web.api.auth

import akka.http.scaladsl.server.directives.Credentials
import by.bsu.utils.RouteService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HTTPBasicAuth {
  def myAdminsPassAuthenticator(credentials: Credentials): Future[Option[String]] =
    credentials match {
      case p@Credentials.Provided(id) =>
        RouteService.adminsService.getPassword(id)
          .map(secret => {
            if (p.verify(secret)) Some(id)
            else None
          })
      case _ => Future.successful(None)
    }
}
