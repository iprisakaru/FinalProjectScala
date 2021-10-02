package by.bsu.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import by.bsu.model.repository.GithubUser
import org.apache.hc.core5.net.URLEncodedUtils
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat, _}

import java.net.URI
import java.nio.charset.Charset
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait GithubAccessJson extends DefaultJsonProtocol with NullOptions {
  implicit val githubTokenJson: RootJsonFormat[GithubAccessToken] = jsonFormat3(GithubAccessToken.apply)

}

case class GithubAccessToken(login: Option[String], id: Option[Long], node_id: Option[String])

class AuthService extends GithubAccessJson {
  private implicit val system: ActorSystem = ActorSystem()

  protected implicit val executor: ExecutionContext = system.dispatcher


  def getToken(code: String): Future[Option[String]] = {

    val accessTokenFieldName = "access_token"

    val request = HttpRequest(method = HttpMethods.POST,
      uri = s"https://github.com/login/oauth/access_token?client_id=5c481d41c1df3a0cc298&client_secret=c674651120f1a7e4431ecca404f273552e3c18cd&code=$code")
    val responseFut = Http(system).singleRequest(request)
    val tokenFut = responseFut.map(_.entity.toStrict(5 seconds)).flatMap(data => data.map(_.data.utf8String))
    val uriFut = tokenFut.map(data => s"?$data")
    val params = uriFut.map(data => URLEncodedUtils.parse(new URI(data), Charset.forName("UTF-8")))

    val accessToken = params.map(_.get(0)).map(data => {
      if (data.getName == accessTokenFieldName) Option(data.getValue)
      else None
    })

    accessToken
  }

  def getIdNameByCode(accessToken: String): Future[Option[String]] = {
    val headerAuth = RawHeader("Authorization", s"Bearer $accessToken")
    val request = HttpRequest(HttpMethods.GET, uri = "https://api.github.com/user").addHeader(headerAuth)
    val responseFut = Http(system).singleRequest(request)
    val entityRequest = responseFut.map(_._3.toStrict(5.seconds)).flatMap(_.map(_.data.utf8String))
    val entitiesByRows = entityRequest.map(_.split("\n").toList)
    val githubInfo = entitiesByRows.map(_.map(_.parseJson)).map(_.map(_.convertTo[GithubAccessToken])).map(_.head)


    val githubUserByToken = githubInfo.map(token => {
      if (token.node_id.nonEmpty) {
        Some(RouteService.githubUsersService.checkByNode(GithubUser(None, token.login.get, token.node_id.get)))
      }
      else None
    }).map(HelpFunctions.fOption(_)).flatten.map(_.flatten)

    githubUserByToken.map(_.flatMap(_.id)).map(_.map(_.toString))
  }
}