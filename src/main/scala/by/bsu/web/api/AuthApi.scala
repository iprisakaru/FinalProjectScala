package by.bsu.web.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import by.bsu.utils.RouteService.authService

import scala.language.postfixOps

trait AuthApi {
  val authApi: Route = {
    get {
      parameter("code") { code =>
        complete(authService.getGHToken(code))
      }
    }

  }
}
