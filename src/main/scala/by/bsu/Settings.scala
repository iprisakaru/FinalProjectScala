package by.bsu

import com.typesafe.config.Config
import org.apache.log4j.Logger

case class Settings(config: Config) {
  val LOGGER = Logger.getLogger(this.getClass.getName)
  private val httpConfig = config.getConfig("http")
  val httpInterface: String = httpConfig.getString("interface")
  val httpPort: Int = httpConfig.getInt("port")
  val httpApiMovieDb: String = httpConfig.getString("httpApiMovieDb")
  val httpSearchMovieApi: String = httpConfig.getString("httpSearchMovieApi")
  LOGGER.info("Configuration file was read successfully.")
}