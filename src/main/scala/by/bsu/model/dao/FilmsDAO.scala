package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Film, FilmsTable, Language, NewFilm}
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

class FilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with FilmsTable with HelpFunctions {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(film: Film): Future[Film] = {
    db.run(films returning films.map(_.filmId) += film)
      .map(id => film.copy(id = Option(id)))
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

  def insertUniqFilmAndLanguages(newFilm: NewFilm): Future[Either[Throwable, Film]] = {

    val languagesDAO = new LanguagesDAO(config)

    val languageInsertion = Try(languagesDAO.insertUniq(Language(None, newFilm.language_name.get)))
    val result = if (languageInsertion.isSuccess) {
      Try(languageInsertion.toOption.get.map(_.map(languageId => insert(Film(None, newFilm.name, newFilm.ageLimit, newFilm.shortDescription, newFilm.timing, newFilm.image, newFilm.releaseDate, newFilm.awards, Option(languageId.get), false))))
        .map(data => foldEitherOfFuture(data)).flatten.map(_.toOption.get)).toEither
    }
    else {
      Try(insert(Film(None, newFilm.name, newFilm.ageLimit, newFilm.shortDescription, newFilm.timing, newFilm.image, newFilm.releaseDate, newFilm.awards, None, false))).toEither
    }

    foldEitherOfFuture(result)
  }

  def deleteAll(): Future[Int] = {
    db.run(films.delete)
  }
}