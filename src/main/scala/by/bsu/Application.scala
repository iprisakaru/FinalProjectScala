package by.bsu

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.logRequestResult
import by.bsu.model.{Db, DbConfiguration}
import by.bsu.web.Routes
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

object Application extends App with DbConfiguration with Db with Routes {


  private implicit val system: ActorSystem = ActorSystem()
  protected implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  val configData: Settings = Settings(ConfigFactory.load)
  val dbConfig = config
  //Logger starts work
  val LOGGER = Logger.getLogger(this.getClass.getName)
  LOGGER.info("Program is running.")
  //starting web api
  LOGGER.info("Web app is running")

  val bindingFuture = Http()
    .bindAndHandle(handler = logRequestResult("log")(routes)
      , interface = configData.httpInterface, port = configData.httpPort)


  LOGGER.info(s"Server online at ${configData.httpInterface}:${configData.httpPort}\n")

}
