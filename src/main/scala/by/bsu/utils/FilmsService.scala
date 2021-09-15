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
    filmsDAO.insertFilm(newFilmWithId)
  }

  def getAll(): Future[Seq[Film]] = {
    filmsDAO.findAll()
  }

  def getById(id: Long): Future[Film] = {
    filmsDAO.findById(id).map(_.get)
  }

  def updateById(id: Long, film: Film): Future[Int] = {
    filmsDAO.update(id, film)
  }

  def deleteById(id: Long): Future[Option[Int]] = {
    filmsDAO.deleteById(id)
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

    val creationOfFilms = result.zip(creationOfActors.zip(creationOfGenres.zip(creationOfCountries.zip(creationOfDirectors.zip(creationOfLanguages)))))
      .flatMap(data => {
        createWithoutFilling(NewFilmWithId(None, data._1.name, Option(data._1.ageLimit.get), data._2._1, data._2._2._1, data._2._2._2._1, data._2._2._2._2._1, data._1.shortDescription, data._1.timing, data._1.image, data._1.releaseDate, data._1.awards, data._2._2._2._2._2, Option(false)))
      }).flatMap(createWithoutFilling)


    creationOfFilms
  }

  def replaceEmptyFilm(newFilm: NewFilmWithFields, filmData: Future[NewFilmWithFields]): Future[NewFilmWithFields] = {
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

    genresFilled.zip(actorsFilled).zip(countriesFilled).zip(directorsFilled).zip(languageFilled)
      .zip(descriptionFilled).zip(awardsFilled).zip(timingFilled).zip(imageFilled).zip(ageLimitFilled).map(zipped =>
      NewFilmWithFields(newFilm.name, zipped._2, zipped._1._1._1._1._1._1._1._1._2, zipped._1._1._1._1._1._1._1._1._1,
        zipped._1._1._1._1._1._1._1._2, zipped._1._1._1._1._1._1._2, zipped._1._1._1._1._2,
        zipped._1._1._2, zipped._1._2, newFilm.releaseDate, zipped._1._1._1._2, zipped._1._1._1._1._1._2))

  }

  def getFilmByNameAndYear(filmName: String, year: Int): Future[NewFilmWithFields] = {
    val data = updateDataController.getAdditionalDataFromApi(filmName, year)
    val result = data.map(_.map(film => NewFilmWithFields(film.Title, Option(film.Rated),
      Option(film.Actors.split(",").toSeq.map(_.trim)), Option(film.Genre.split(",").toSeq.map(_.trim)), Option(film.Country.split(",").toSeq.map(_.trim)), Option(film.Director.split(",").toSeq.map(_.trim)),
      Option(film.Plot), Option(film.Runtime), Option(film.Poster), film.Released, Option(film.Awards), Option(film.Language))))

    (result.map(_.head))
  }

  def updateFilmsPerDay(): Future[List[NewFilmWithId]] = {
    val data = updateDataController.periodicUpdateData()
    data._1.map(_.map(name => NewFilmWithId(None, name, None, None, None, None, None, None, None, None, data._2, None, None, Option(false))))
  }
}
