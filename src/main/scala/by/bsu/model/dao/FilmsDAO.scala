package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Film, FilmsTable, NewFilm}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

class FilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(film: Film): Future[Film] = {
    db.run(films returning films.map(_.film_id) += film)
      .map(id => film.copy(id = Option(id)))
  }

  def update(id: Long, film: Film): Future[Int] = {
    db.run(films.filter(_.film_id === id).map(customer => (customer.name, customer.age_limit, customer.short_description, customer.timing, customer.image, customer.release_date, customer.awards, customer.language_id))
      .update((film.name, film.age_limit, film.short_description, film.timing, film.image, film.release_date, film.awards, film.language_id)))
  }

  def findAll(): Future[Seq[Film]] = db.run(films.result)

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

  def insertUniq(newFilm: NewFilm) = {
    val tryingToInsert = Try(db.run(languages.result.map(data =>
      (data.find(language => language.name == newFilm.language_name).head.id.get))
      .map(data => insert(Film(None, newFilm.name, newFilm.age_limit, newFilm.short_description, newFilm.timing, newFilm.image, newFilm.release_date, newFilm.awards, data)))).flatten)

    if (tryingToInsert.isSuccess) Right(tryingToInsert.toOption.get)
    else {
      Left(new Exception + s"Foreign keys for trip of user ${newFilm.name} are impossible to find")
    }

  }

  def deleteAll(): Future[Int] = {
    db.run(films.delete)
  }
}