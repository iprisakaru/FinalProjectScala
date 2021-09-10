package by.bsu

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.logRequestResult
import akka.stream.ActorMaterializer
import by.bsu.model.repository.NewFilm
import by.bsu.model.{Db, DbConfiguration}
import by.bsu.utils.FilmService
import by.bsu.web.Routes
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps

object Application extends App with DbConfiguration with Db with Routes with FilmService {

}
