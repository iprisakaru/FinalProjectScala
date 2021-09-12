package by.bsu.utils

import by.bsu.Application.LOGGER
import by.bsu.model.Db
import by.bsu.model.dao._
import by.bsu.model.repository._
import by.bsu.web.api.UpdatingDataController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Try

trait FilmService extends Db with HelpFunctions with LinkedTablesDbService {

  val updateDataController = new UpdatingDataController

  val filmsDAO = new FilmsDAO(config)
  val actorsDAO = new ActorsDAO(config)
  val directorsDAO = new DirectorsDAO(config)
  val countriesDAO = new CountriesDAO(config)
  val genresDAO = new GenresDAO(config)
  val languagesDAO = new LanguagesDAO(config)


  def createFilmWithoutFilling(newFilm: NewFilm): Future[Film] = {
    LOGGER.debug(s"Creating film without filling fields of ${newFilm.name} of ${newFilm.releaseDate}")
    val actorsInsertion: Try[Future[Seq[Actor]]] = Try(Future.sequence(newFilm.actors.get.map(actor => actorsDAO.insertUniq(Actor(None, actor)))))
    val genresInsertion = Try(Future.sequence(newFilm.genres.get.map(genre => genresDAO.insertUniq(Genre(None, genre)))))
    val directorsInsertion: Try[Future[Seq[Director]]] = Try(Future.sequence(newFilm.directors.get.map(director => directorsDAO.insertUniq(Director(None, director)))))
    val countriesInsertion: Try[Future[Seq[Country]]] = Try(Future.sequence(newFilm.countries.get.map(country => countriesDAO.insertUniq(Country(None, country)))))
    val filmsInsertion = filmsDAO.insertUniqFilmAndLanguages(newFilm)
    val filmsEntitiesTables = createLinkedTables(filmsInsertion, actorsInsertion, genresInsertion, directorsInsertion, countriesInsertion)
    if (filmsInsertion.isCompleted) LOGGER.debug(s"Film insertion is completed")
    (filmsInsertion.map(_.toOption.get))
  }

  def createFilmWithFilling(newFilm: NewFilm): Future[Film] = {
    LOGGER.debug(s"Creating film without filling fields of ${newFilm.name} of ${newFilm.releaseDate}")
    val filmData = getFilmByNameAndYear(newFilm.name, newFilm.releaseDate.toInt)
    LOGGER.trace("Checking fields for None")
    val genresFilled = if (newFilm.genres.isEmpty) filmData.map(_.genres) else Future(newFilm.genres)
    val actorsFilled = if (newFilm.actors.isEmpty) filmData.map(_.actors) else Future(newFilm.actors)
    val countriesFilled = if (newFilm.countries.isEmpty) filmData.map(_.countries) else Future(newFilm.countries)
    val directorsFilled = if (newFilm.directors.isEmpty) filmData.map(_.directors) else Future(newFilm.directors)
    val languageFilled = if (newFilm.language_name.isEmpty) filmData.map(_.language_name) else Future(newFilm.language_name)
    val descriptionFilled = if (newFilm.directors.isEmpty) filmData.map(_.shortDescription) else Future(newFilm.shortDescription)
    val awardsFilled = if (newFilm.awards.isEmpty) filmData.map(_.awards) else Future(newFilm.awards)
    val timingFilled = if (newFilm.timing.isEmpty) filmData.map(_.timing) else Future(newFilm.timing)
    val imageFilled = if (newFilm.image.isEmpty) filmData.map(_.image) else Future(newFilm.image)
    val ageLimitFilled = if (newFilm.ageLimit.isEmpty) filmData.map(_.ageLimit) else Future(newFilm.ageLimit)

    val result = genresFilled.zip(actorsFilled).zip(countriesFilled).zip(directorsFilled).zip(languageFilled)
      .zip(descriptionFilled).zip(awardsFilled).zip(timingFilled).zip(imageFilled).zip(ageLimitFilled).map(zipped =>
      NewFilm(newFilm.name, zipped._2, zipped._1._1._1._1._1._1._1._1._2, zipped._1._1._1._1._1._1._1._1._1,
        zipped._1._1._1._1._1._1._1._2, zipped._1._1._1._1._1._1._2, zipped._1._1._1._1._2,
        zipped._1._1._2, zipped._1._2, newFilm.releaseDate, zipped._1._1._1._2, zipped._1._1._1._1._1._2))

    result.map(createFilmWithoutFilling).flatten
  }


  def getAllFilms: Future[Seq[Film]] = {
    filmsDAO.findAll()
  }

  def getFilmById(id: Long): Future[Option[Film]] = {
    filmsDAO.findById(id)
  }

  def deleteById(id: Long): Future[Boolean] = {
    filmsDAO.deleteById(id)
  }

  def deleteAll(): Future[List[Int]] = {
    LOGGER.debug("Deleting all data")
    Future.sequence(List(genresFilmsDAO.deleteAll(),
      directorsFilmsDAO.deleteAll(),
      countriesFilmsDAO.deleteAll(),
      actorsFilmsDAO.deleteAll(),
      actorsDAO.deleteAll(),
      genresDAO.deleteAll(),
      directorsDAO.deleteAll(),
      countriesDAO.deleteAll(),
      filmsDAO.deleteAll(),
      languagesDAO.deleteAll()))
  }

  def updateFilmsPerDay(): Future[List[Film]] = {
    val data = updateDataController.periodicUpdateData()
    data._1.flatMap(fut => Future.sequence(fut.map(line => filmsDAO.insertUniqFilmAndLanguages(NewFilm(line, None, None, None, None, None, None, None, None, data._2, None, None)).filter(_.isRight).map(_.toOption.get))))
  }

  def getGenresFromApi = {
    updateDataController.getGenresFromApi().flatMap(fut => Future.sequence(fut.map(tmp => Future.sequence(tmp.genres.map(genre => genresDAO.insertUniq(Genre(None, genre.name)))))))
  }

  def getFilmByNameAndYear(filmName: String, year: Int): Future[NewFilm] = {
    val data = updateDataController.getAdditionalDataFromApi(filmName, year)
    val result = data.map(_.map(film => NewFilm(film.Title, Option(film.Rated),
      Option(film.Actors.split(",").toSeq.map(_.trim)), Option(film.Genre.split(",").toSeq.map(_.trim)), Option(film.Country.split(",").toSeq.map(_.trim)), Option(film.Director.split(",").toSeq.map(_.trim)),
      Option(film.Plot), Option(film.Runtime), Option(film.Poster), film.Released, Option(film.Awards), Option(film.Language))))
    result.map(_.head)
  }

}

