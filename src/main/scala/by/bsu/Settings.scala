package by.bsu

import com.typesafe.config.Config
import org.apache.log4j.Logger

case class Settings(config: Config) {
  val LOGGER = Logger.getLogger(this.getClass.getName)
  private val httpConfig = config.getConfig("http")
  val httpInterface: String = httpConfig.getString("interface")
  val httpPort: Int = httpConfig.getInt("port")
  val httpApiMovieDb: String = httpConfig.getString("httpApiMovieDb")
  val httpMovieUpdateApi: String = httpConfig.getString("httpMovieUpdateApi")

  val githubClientId: String = config.getString("apis.github.clientId")
  val githubClientSecret: String = config.getString("apis.github.clientSecret")
  val githubUri: String = config.getString("apis.github.uri")
  val githubTokenUri: String = config.getString("apis.github.tokenUri")
  val googleUri: String = config.getString("apis.google.uri")
  LOGGER.info("Configuration file was read successfully.")
}