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

object Application extends App with DbConfiguration with Db with Routes with FilmService{



  private implicit val system: ActorSystem = ActorSystem()
  protected implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()
  val configData: Settings = Settings(ConfigFactory.load)

  //Logger starts work
  val LOGGER = Logger.getLogger(this.getClass.getName)
  LOGGER.info("Program is running.")
  deleteAll()

  //starting web api
  LOGGER.info("Web app is running")
  val bindingFuture = Http()
    .bindAndHandle(handler = logRequestResult("log")(routes)
      , interface = configData.httpInterface, port = configData.httpPort)

  LOGGER.info(s"Server online at ${configData.httpInterface}:${configData.httpPort}\n")

}
