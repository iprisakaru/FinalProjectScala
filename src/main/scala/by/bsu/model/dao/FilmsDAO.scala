package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Film, FilmsTable, Language, NewFilm, NewFilmWithoutId}
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
    db.run(films returning films.map(_.film_id) += film)
      .map(id => film.copy(id = Option(id)))
  }

  def update(id: Long, film: Film, visible: Boolean ): Future[Int] = {
    db.run(films.filter(_.film_id === id).map(customer => (customer.name, customer.age_limit.?, customer.short_description, customer.timing.?, customer.image.?, customer.release_date, customer.awards.?, customer.language_id.?, visible))
      .update((film.name, film.age_limit, film.short_description, film.timing, film.image, film.release_date, film.awards, film.language_id, visible)))
  }

  def findAll(): Future[Seq[Film]] = db.run(films.filter(_.isPublic === true).result)

  def deleteById(id: Long): Future[Boolean] = {
    db.run(films.filter(_.film_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Long): Future[Option[Film]] = {
    db.run(films.filter(_.film_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Film]] = {
    db.run(films.filter(_.name === name).result.headOption)
  }

  def insertUniqFilmAndLanguages(newFilm: NewFilm): Future[Either[Throwable, Film]] = {

    val languagesDAO = new LanguagesDAO(config)

    val languageInsertion = languagesDAO.insertUniq(Language(None, newFilm.language_name))

    val tryingToInsert = Try(languageInsertion.map(_.map(languageId =>insert(Film(None, newFilm.name, Option(newFilm.age_limit), newFilm.short_description, Option(newFilm.timing), Option(newFilm.image), newFilm.release_date, Option(newFilm.awards), Option(languageId.get), false))))
     .map(data=> foldEitherOfFuture(data)).flatten.map(_.toOption.get))

     foldEitherOfFuture(tryingToInsert.toEither)


  }

  def deleteAll(): Future[Int] = {
    db.run(films.delete)
  }
}