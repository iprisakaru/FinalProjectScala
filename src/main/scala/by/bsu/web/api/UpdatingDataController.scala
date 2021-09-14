package by.bsu.web.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import by.bsu.Application.configData
import by.bsu.utils.HelpFunctions
import by.bsu.utils.HelpFunctions.isEnglish
import org.apache.log4j.Logger
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat, _}

import java.time.{ZoneId, ZonedDateTime}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

trait DataJsonMapping extends DefaultJsonProtocol with NullOptions {
  implicit val fromApiEverydayJsonFormat: RootJsonFormat[DataFromAPIEveryday] = jsonFormat3(DataFromAPIEveryday.apply)
  implicit val genresFromApiEverydayJsonFormat: RootJsonFormat[GenreFromApi] = jsonFormat2(GenreFromApi.apply)
  implicit val genresApiJsonFormat: RootJsonFormat[GenresFromApi] = jsonFormat1(GenresFromApi.apply)
  implicit val filmsApiJsonFormat: RootJsonFormat[FilmFromApi] = jsonFormat14(FilmFromApi.apply)

}

case class DataFromAPIEveryday(original_name: String, popularity: Double, id: Int)

case class GenreFromApi(id: Long, name: String)

case class GenresFromApi(genres: Seq[GenreFromApi])

case class FilmFromApi(Title: String, Year: String, Rated: String, Released: String, Runtime: String,
                       Genre: String, Director: String, Writer: String, Actors: String,
                       Plot: String, Language: String, Country: String, Awards: String,
                       Poster: String)

class UpdatingDataController extends DataJsonMapping {

  val LOGGER = Logger.getLogger(this.getClass.getName)

  type CurrentTime = String

  private implicit val system: ActorSystem = ActorSystem()

  protected implicit val executor: ExecutionContext = system.dispatcher

  object Yesterday {
    private val yesterday = ZonedDateTime.now(ZoneId.of("UTC+3")).minusDays(1)
    val currentDay = if (yesterday.getDayOfMonth < 10) "0" + yesterday.getDayOfMonth.toString
    else yesterday.getDayOfMonth.toString
    val currentMonth = if (yesterday.getMonth.getValue < 10) "0" + yesterday.getMonth.getValue.toString
    else yesterday.getMonth.getValue.toString
    val currentYear = yesterday.getYear.toString
  }

  def periodicUpdateData(): (Future[List[String]], CurrentTime) = {
    val api = configData.httpApiMovieDb
    LOGGER.debug(s"Running periodic update of data ${Yesterday.currentDay}.${Yesterday.currentMonth}")

    val request = HttpRequest(method = HttpMethods.GET,
      uri = s"https://files.tmdb.org/p/exports/tv_series_ids_${Yesterday.currentMonth}_${Yesterday.currentDay}_${Yesterday.currentYear}.json.gz?api_key=$api")
    LOGGER.debug(s"Request is response? -${request.isResponse()}")
    val compressedRequest = Gzip.decodeMessage(request)
    val responseFuture = Http(system).singleRequest(compressedRequest)
    val entityRequest = responseFuture.flatMap(_._3.toStrict(5.seconds)).flatMap(entity => Gzip.decode(entity.data).map(_.utf8String))
    val entitiesByRows = entityRequest.map(_.split("\n").toList)
    val listOfData = entitiesByRows.map(_.map(line => line.parseJson))
      .map(_.map(_.convertTo[DataFromAPIEveryday])).map(getDataFromApiEveryday)

    (listOfData, s"${Yesterday.currentYear}")
  }

  def getDataFromApiEveryday(listOfData: List[DataFromAPIEveryday]) = {
    listOfData.map(_.original_name).filter(data => isEnglish(data))
  }

  def getGenresFromApi(): Future[List[GenresFromApi]] = {
    LOGGER.debug(s"Getting genres from API")
    val api = configData.httpApiMovieDb
    val request = HttpRequest(method = HttpMethods.GET,
      uri = s"https://api.themoviedb.org/3/genre/movie/list?api_key=$api&language=en-US")
    LOGGER.debug(s"Request is response? -${request.isResponse()}")
    val responseFut = Http(system).singleRequest(request)
    val entityRequest = responseFut.map(_._3.toStrict(5.seconds)).flatMap(_.map(_.data.utf8String))
    val entitiesByRows = entityRequest.map(_.split("\n").toList)
    val listOfData = entitiesByRows.map(_.map(_.parseJson)).map(_.map(_.convertTo[GenresFromApi]))

    listOfData
  }

  def getAdditionalDataFromApi(filmName: String, releaseYear: Int): Future[Seq[FilmFromApi]] = {
    LOGGER.debug(s"Getting additional data from API for ${filmName} of ${releaseYear} year")
    val api = configData.httpMovieUpdateApi
    val request = HttpRequest(method = HttpMethods.GET,
      uri = s"https://www.omdbapi.com/?apikey=$api&t=${filmName.replaceAll(" ", "+")}&y=$releaseYear",
    )
    LOGGER.debug(s"Request is response? -${request.isResponse()}")
    val responseFut = Http(system).singleRequest(request)
    val entityRequest = responseFut.map(_._3.toStrict(5.seconds)).flatMap(data => data.map(_.data.utf8String))
    val entitiesByRows = entityRequest.map(_.split("\n").toSeq)
    val listOfData = entitiesByRows.map(_.map(line => line.parseJson)).map(_.map(_.convertTo[FilmFromApi]))

    listOfData
  }
}