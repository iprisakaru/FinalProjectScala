package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{ActorFilm, _}
import by.bsu.utils.HelpFunctions
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class FilmsDAO(override val config: DatabaseConfig[JdbcProfile])
  extends Db with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  private val actorsFilmsDAO = new ActorsFilmsDAO(config)
  private val countriesFilmsDAO = new CountriesFilmsDAO(config)
  private val directorsFilmsDAO = new DirectorsFilmsDAO(config)
  private val genresFilmsDAO = new GenresFilmsDAO(config)

  def insertFilm(film: NewFilmWithId): Future[NewFilmWithId] = {
    LOGGER.debug(s"Inserting film ${film.name}")

    val actorsId = 0
    val genresId = 1
    val directorsId = 2
    val countriesId = 3

    val result = db.run(films returning films.map(_.filmId) += Film(film.id, film.name, film.ageLimit, film.shortDescription, film.timing, film.image,
      film.releaseDate, film.awards, film.languageId, Option(false)))
      .map(id => film.copy(id = Option(id)))
    result.flatMap(data => insertLinkedTables(data)).map(data => film.copy(actorsId = data(actorsId),
      genresId = data(genresId), directorsId = data(directorsId),
      countriesId = data(countriesId)))
  }

  private def insertLinkedTables(film: NewFilmWithId) = {
    val actorsListForInsertion = HelpFunctions.fOption(film.actorsId.map(_.map(data =>
      ActorFilm(None, data, film.id.get))).map(actorsFilmsDAO.insertListActorFilm))
      .map(_.flatten).map(_.map(_.map(_.actorId)))

    val genresListForInsertion = HelpFunctions.fOption(film.genresId.map(_.map(data =>
      GenreFilm(None, data, film.id.get))).map(genresFilmsDAO.insertListGenresFilm))
      .map(_.flatten).map(_.map(_.map(_.genreId)))

    val directorsListForInsertion = HelpFunctions.fOption(film.directorsId.map(_.map(data =>
      DirectorFilm(None, data, film.id.get))).map(directorsFilmsDAO.insertListDirectorFilm))
      .map(_.flatten).map(_.map(_.map(_.directorId)))

    val countriesListForInsertion = HelpFunctions.fOption(film.countriesId.map(_.map(data =>
      CountryFilm(None, data, film.id.get))).map(countriesFilmsDAO.insertListCountryFilm))
      .map(_.flatten).map(_.map(_.map(_.countryId)))

    Future.sequence(List(actorsListForInsertion, genresListForInsertion, directorsListForInsertion, countriesListForInsertion))
  }


  def update(id: Long, film: Film): Future[Int] = {
    LOGGER.debug(s"Updating film $id id")
    db.run(films.filter(_.filmId === id).map(customer => (customer.name, customer.ageLimit, customer.shortDescription, customer.timing, customer.image, customer.releaseDate, customer.awards, customer.languageId, customer.public))
      .update((film.name, film.ageLimit, film.shortDescription, film.timing, film.image, film.releaseDate, film.awards, film.languageId, film.isPublic)))
  }

  def changeVisibility(id: Long, isVisible: Boolean) = {
    db.run(films.filter(_.filmId === id).map(_.public).update(Option(isVisible)))
  }

  def findAll(isPublic: Boolean): Future[Seq[Film]] = db.run(films.filter(_.public === isPublic).result)

  def findAll() = {
    db.run(films.result)
  }

  def deleteById(id: Long) = {
    db.run(deleteByIdLinkedTablesQuery(id)).map(_.toOption)
  }

  private def deleteByIdLinkedTablesQuery(id: Long) = {
    (for {
      genresDeleted <- genresFilmsDAO.deleteByFilmIdQuery(id).asTry
      directorsDeleted <- directorsFilmsDAO.deleteByFilmIdQuery(id).asTry
      countriesDeleted <- countriesFilmsDAO.deleteByFilmIdQuery(id).asTry
      actorsDeleted <- actorsFilmsDAO.deleteByFilmIdQuery(id)
      filmsDeleted <- deleteByFilmIdQuery(id).asTry
    } yield (filmsDeleted)).transactionally
  }

  def deleteByFilmIdQuery(id: Long) = {
    films.filter(e => e.filmId === id).delete
  }

  def findById(id: Long): Future[Option[Film]] = {
    db.run(films.filter(_.filmId === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Film]] = {
    db.run(films.filter(_.name === name).result.headOption)
  }


  def deleteAllFilms(): Future[Int] = {
    db.run(films.delete)
  }
}