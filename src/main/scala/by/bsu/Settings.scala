package by.bsu
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.log4j.Logger

import scala.util.Try

case class Settings(config: Config) {
  val LOGGER = Logger.getLogger(this.getClass.getName)
  private val httpConfig = config.getConfig("http")
  val httpInterface: String = httpConfig.getString("interface")
  val httpPort: Int = httpConfig.getInt("port")

  LOGGER.info("Configuration file was read successfully.")
}