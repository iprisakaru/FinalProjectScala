package by.bsu.model.dao

import by.bsu.model.repository._
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class FilmsDAO(override val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  override type T = Film

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def insert(film: Film): Future[Option[Film]] = {
    LOGGER.debug(s"Inserting film ${film.name}")
    val result = db.run(((films returning films) += film).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(film.name)
    }).flatten
  }

  def update(id: Int, film: Film): Future[Int] = {
    LOGGER.debug(s"Updating film $id id")
    db.run(films.filter(_.filmId === id).map(customer => (customer.name, customer.ageLimit, customer.shortDescription, customer.timing, customer.image, customer.releaseDate, customer.awards, customer.languageId, customer.public))
      .update((film.name, film.ageLimit, film.shortDescription, film.timing, film.image, film.releaseDate, film.awards, film.languageId, film.isPublic)))
  }

  def changeVisibility(id: Int, isVisible: Boolean) = {
    db.run(films.filter(_.filmId === id).map(_.public).update(Option(isVisible)))
  }

  def findAll(isPublic: Boolean): Future[Seq[Film]] = db.run(films.filter(_.public === isPublic).result)

  def findAll() = {
    db.run(films.result)
  }

  def deleteById(id: Int) = {
    db.run(films.delete) map {
      _ > 0
    }
  }

  def deleteByFilmIdQuery(id: Int) = {
    films.filter(e => e.filmId === id).delete
  }

  def findById(id: Int): Future[Option[Film]] = {
    db.run(films.filter(_.filmId === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Film]] = {
    db.run(films.filter(_.name === name).result.headOption)
  }

  def findAllWithLanguage(): Future[Seq[(Film, Option[Language])]] = {
    db.run(films.joinLeft(languages).on(_.languageId === _.language_id).result)
  }

  override def deleteAll(): Future[Int] = {
    db.run(films.delete)
  }

  override def insertList(entities: Seq[Film]): Future[Seq[Option[Film]]] = ???
}