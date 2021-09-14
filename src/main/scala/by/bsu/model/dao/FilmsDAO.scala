package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository._
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class FilmsDAO(override val config: DatabaseConfig[JdbcProfile])
  extends Db with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val actorsFilmsDAO = new ActorsFilmsDAO(config)
  val countriesFilmsDAO = new CountriesFilmsDAO(config)
  val directorsFilmsDAO = new DirectorsFilmsDAO(config)
  val genresFilmsDAO = new GenresFilmsDAO(config)

  def insertFilm(film: NewFilmWithId): Future[NewFilmWithId] = {

    val result = db.run(films returning films.map(_.filmId) += Film(film.id, film.name, film.ageLimit, film.shortDescription, film.timing, film.image,
      film.releaseDate, film.awards, film.languageId, false))
      .map(id => film.copy(id = Option(id)))
    result.flatMap(data => insertLinkedTables(data)).map(data => film.copy(actorsId = data(0), genresId = data(1), directorsId = data(2), countriesId = data(3)))
  }

  private def insertLinkedTables(film: NewFilmWithId) = {
    val actorsListForInsertion = HelpFunctions.fOption(film.actorsId.map(_.map(data =>
      ActorFilm(None, data, film.id.get))).map(actorsFilmsDAO.insertListActorFilm))
      .map(_.flatten).map(_.map(_.map(_.actorId)))
    
    val genresListForInsertion = HelpFunctions.fOption(film.genresId.map(_.map(data =>
      GenreFilm(None, data, film.id.get))).map(genresFilmsDAO.insertListGenresFilm))
      .map(_.flatten).map(_.map(_.map(_.genreId)))

    val directorsListForInsertion = HelpFunctions.fOption(film.directorsId.map(_.map(data =>
      DirectorFilm(None, data, film.id.get))).map(directorsFilmsDAO.insertListDirectorsFilm))
      .map(_.flatten).map(_.map(_.map(_.directorId)))

    val countriesListForInsertion = HelpFunctions.fOption(film.countriesId.map(_.map(data =>
      CountryFilm(None, data, film.id.get))).map(countriesFilmsDAO.insertListCountryFilm))
      .map(_.flatten).map(_.map(_.map(_.countryId)))

    Future.sequence(List(actorsListForInsertion, genresListForInsertion, directorsListForInsertion, countriesListForInsertion))
  }


  def update(id: Long, film: Film): Future[Int] = {
    db.run(films.filter(_.filmId === id).map(customer => (customer.name, customer.ageLimit, customer.shortDescription, customer.timing, customer.image, customer.releaseDate, customer.awards, customer.languageId, customer.public))
      .update((film.name, film.ageLimit, film.shortDescription, film.timing, film.image, film.releaseDate, film.awards, film.languageId, film.isPublic)))
  }

  def findAll(): Future[Seq[Film]] = db.run(films.filter(_.public === false).result)

  def deleteById(id: Long): Future[Boolean] = {
    db.run(films.filter(_.filmId === id).delete) map {
      _ > 0
    }
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