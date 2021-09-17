package by.bsu.utils

import by.bsu.model.dao.FilmsDAO
import by.bsu.model.repository._
import by.bsu.utils.RouteService._
import by.bsu.web.api.UpdatingDataController
import org.apache.log4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FilmsService(filmsDAO: FilmsDAO) {

  val updateDataController = new UpdatingDataController

  var LOGGER = Logger.getLogger(this.getClass.getName)

  def createWithoutFilling(newFilmWithId: NewFilmWithId): Future[NewFilmWithId] = {
    LOGGER.trace(s"Creating film without filling fields")
    filmsDAO.insertFilm(newFilmWithId)
  }

  def getAllPublic(): Future[Seq[Film]] = {
    LOGGER.trace(s"Getting all public films")
    filmsDAO.findAll(true)
  }

  def getAllPrivate(): Future[Seq[Film]] = {
    LOGGER.trace(s"Getting all private films")
    filmsDAO.findAll(false)
  }

  def getAll(): Future[Seq[Film]] = {
    LOGGER.trace(s"Getting all films")
    filmsDAO.findAll()
  }

  def getById(id: Long): Future[Film] = {
    LOGGER.trace(s"Getting film with $id id")
    filmsDAO.findById(id).map(_.get)
  }

  def updateById(id: Long, film: Film): Future[Int] = {
    LOGGER.trace(s"Updating film with $id id")
    filmsDAO.update(id, film)
  }

  def deleteById(id: Long): Future[Option[Int]] = {
    LOGGER.trace(s"Deleting film with $id")
    filmsDAO.deleteById(id)
  }

  def makePublic(id: Long) = {
    LOGGER.trace(s"Making film $id id public")
    filmsDAO.changeVisibility(id, true)
  }

  def makePrivate(id: Long) = {
    LOGGER.trace(s"Making film $id id private")
    filmsDAO.changeVisibility(id, false)
  }

  def createFilmWithFilling(newFilm: NewFilmWithFields): Future[NewFilmWithId] = {
    LOGGER.debug(s"Creating film without filling fields of ${newFilm.name} of ${newFilm.releaseDate}")
    val filmData = getFilmByNameAndYear(newFilm.name, newFilm.releaseDate.toInt)
    LOGGER.trace("Checking fields for None")
    val result = replaceEmptyFilm(newFilm, filmData)
    val creationOfActors = result.map(_.actors.get).map(_.map(name => Actor(None, name))).flatMap(actor => actorsService.createList(actor)).map(_.map(_.map(_.id.get)))
    val creationOfGenres = result.map(_.genres.get).map(_.map(name => Genre(None, name))).flatMap(value => genresService.createList(value)).map(_.map(_.map(_.id.get)))
    val creationOfCountries = result.map(_.countries.get).map(_.map(name => Country(None, name))).flatMap(value => countriesService.createList(value)).map(_.map(_.map(_.id.get)))
    val creationOfDirectors = result.map(_.directors.get).map(_.map(name => Director(None, name))).flatMap(value => directorsService.createList(value)).map(_.map(_.map(_.id.get)))
    val creationOfLanguages = result.map(_.languageName.get).flatMap(value => languagesService.create(Language(None, value))).map(_.map(_.id.get))

    val creationOfFilms = for {
      resultFut <- result
      creationOfActorsFut <- creationOfActors
      creationOfGenresFut <- creationOfGenres
      creationOfCountriesFut <- creationOfCountries
      creationOfDirectorsFut <- creationOfDirectors
      creationOfLanguagesFut <- creationOfLanguages

    } yield (createWithoutFilling(NewFilmWithId(None, resultFut.name, resultFut.ageLimit, creationOfActorsFut,
      creationOfGenresFut, creationOfCountriesFut, creationOfDirectorsFut, resultFut.shortDescription,
      resultFut.timing, resultFut.image, resultFut.releaseDate, resultFut.awards, creationOfLanguagesFut, Option(false))))

    creationOfFilms.flatten
  }

  def replaceEmptyFilm(newFilm: NewFilmWithFields, filmData: Future[NewFilmWithFields]): Future[NewFilmWithFields] = {

    LOGGER.trace(s"Replacing empty fields of film")

    val genresFilled = if (newFilm.genres.isEmpty) filmData.map(_.genres) else Future(newFilm.genres)
    val actorsFilled = if (newFilm.actors.isEmpty) filmData.map(_.actors) else Future(newFilm.actors)
    val countriesFilled = if (newFilm.countries.isEmpty) filmData.map(_.countries) else Future(newFilm.countries)
    val directorsFilled = if (newFilm.directors.isEmpty) filmData.map(_.directors) else Future(newFilm.directors)
    val languageFilled = if (newFilm.languageName.isEmpty) filmData.map(_.languageName) else Future(newFilm.languageName)
    val descriptionFilled = if (newFilm.directors.isEmpty) filmData.map(_.shortDescription) else Future(newFilm.shortDescription)
    val awardsFilled = if (newFilm.awards.isEmpty) filmData.map(_.awards) else Future(newFilm.awards)
    val timingFilled = if (newFilm.timing.isEmpty) filmData.map(_.timing) else Future(newFilm.timing)
    val imageFilled = if (newFilm.image.isEmpty) filmData.map(_.image) else Future(newFilm.image)
    val ageLimitFilled = if (newFilm.ageLimit.isEmpty) filmData.map(_.ageLimit) else Future(newFilm.ageLimit)

    for {
      genresFilledFut <- genresFilled
      actorsFilledFut <- actorsFilled
      countriesFilledFut <- countriesFilled
      directorsFilledFut <- directorsFilled
      languageFilledFut <- languageFilled
      descriptionFilledFut <- descriptionFilled
      awardsFilledFut <- awardsFilled
      timingFilledFut <- timingFilled
      imageFilledFut <- imageFilled
      ageLimitFilledFut <- ageLimitFilled
    } yield (NewFilmWithFields(newFilm.name, ageLimitFilledFut, actorsFilledFut, genresFilledFut, countriesFilledFut,
      directorsFilledFut, descriptionFilledFut, timingFilledFut, imageFilledFut,
      newFilm.releaseDate, awardsFilledFut, languageFilledFut))
  }

  def getFilmByNameAndYear(filmName: String, year: Int): Future[NewFilmWithFields] = {

    LOGGER.trace(s"Trying to more info about film $filmName - $year")
    val data = updateDataController.getAdditionalDataFromApi(filmName, year)
    val result = data.map(_.map(film => NewFilmWithFields(film.Title, Option(film.Rated),
      Option(film.Actors.split(",").toSeq.map(_.trim)), Option(film.Genre.split(",").toSeq.map(_.trim)), Option(film.Country.split(",").toSeq.map(_.trim)), Option(film.Director.split(",").toSeq.map(_.trim)),
      Option(film.Plot), Option(film.Runtime), Option(film.Poster), film.Released, Option(film.Awards), Option(film.Language))))

    (result.map(_.head))
  }

  def updateFilmsPerDay(): Future[List[NewFilmWithId]] = {
    LOGGER.trace(s"Trying to load genres from API")
    val data = updateDataController.periodicUpdateData()
    data._1.map(_.map(name => NewFilmWithId(None, name, None, None, None, None, None, None, None, None, data._2, None, None, Option(false))))
  }
}
