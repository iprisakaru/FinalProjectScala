package by.bsu.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import by.bsu.model.repository.User
import org.apache.hc.core5.net.URLEncodedUtils
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat, _}

import java.net.URI
import java.nio.charset.Charset
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try

trait GithubAccessJson extends DefaultJsonProtocol with NullOptions {
  implicit val githubTokenJson: RootJsonFormat[GithubAccessToken] = jsonFormat3(GithubAccessToken.apply)
  implicit val googleTokenJson: RootJsonFormat[GoogleAccessToken] = jsonFormat2(GoogleAccessToken.apply)

}

case class GithubAccessToken(login: Option[String], id: Option[Long], node_id: Option[String])

case class GoogleAccessToken(id: Option[String], email: Option[String])

class AuthService extends GithubAccessJson {
  private implicit val system: ActorSystem = ActorSystem()

  protected implicit val executor: ExecutionContext = system.dispatcher


  def getGHToken(code: String): Future[Option[String]] = {

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

  def getGitHubIdNameByToken(accessToken: String): Future[Option[String]] = {
    val headerAuth = RawHeader("Authorization", s"Bearer $accessToken")
    val request = HttpRequest(HttpMethods.GET, uri = "https://api.github.com/user").addHeader(headerAuth)
    val responseFut = Http(system).singleRequest(request)
    val entityRequest = responseFut.map(_._3.toStrict(5.seconds)).flatMap(_.map(_.data.utf8String))
    val entitiesByRows = entityRequest.map(_.split("\n").toList)

    val githubInfo = entitiesByRows.map(_.map(_.parseJson)).map(_.map(_.convertTo[GithubAccessToken])).map(_.head)

    val githubUserByToken = githubInfo.map(token => {
      if (token.node_id.nonEmpty) {
        Some(RouteService.githubUsersService.checkGithubUserByNode(User(None, token.login.get, token.node_id, None)))
      }
      else None
    }).map(HelpFunctions.fOption(_)).flatten.map(_.flatten)

    githubUserByToken.map(_.flatMap(_.id)).map(_.map(_.toString))
  }

  def getGoogleIdEmailByToken(accessToken: String): Future[Option[String]] = {
    val headerAuth = RawHeader("Authorization", s"Bearer $accessToken")
    val request = HttpRequest(HttpMethods.GET, uri = "https://www.googleapis.com/oauth2/v2/userinfo").addHeader(headerAuth)
    val responseFut = Http(system).singleRequest(request)
    val entityRequest = responseFut.map(_._3.toStrict(5.seconds)).flatMap(_.map(_.data.utf8String))
    val entitiesByRows = entityRequest
    val google = entitiesByRows.map(_.parseJson).map(_.convertTo[GoogleAccessToken])

    val googleUserByToken = google.map(token => {
      if (token.email.nonEmpty && token.id.nonEmpty) {
        Some(RouteService.githubUsersService.checkGoogleUserById(User(None, token.email.get, None, token.id)))
      }
      else None
    }).map(HelpFunctions.fOption(_)).flatten.map(_.flatten)

    googleUserByToken.map(_.flatMap(_.id)).map(_.map(_.toString))
  }

  def authByToken(accessToken: String): Future[Option[String]] = {
    val tryingGithub = HelpFunctions.fOption(Try(getGitHubIdNameByToken(accessToken)).toOption).map(_.flatten)
    val tryingGoogle = HelpFunctions.fOption(Try(getGoogleIdEmailByToken(accessToken)).toOption).map(_.flatten)

    for {
      tryingGithubFut <- tryingGithub
      tryingGoogleFut <- tryingGoogle
    } yield (if (tryingGithubFut.nonEmpty) tryingGithubFut
    else if (tryingGoogleFut.nonEmpty) tryingGoogleFut
    else None)

  }

}