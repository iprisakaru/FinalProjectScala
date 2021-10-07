package by.bsu.model.dao

import by.bsu.model.repository._
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class FilmsDAO(override val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with FilmsTable with CommentsTable {

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

  def findAll(isPublic: Boolean) = db.run(films.filter(_.public === isPublic)
    .joinLeft(languages).on(_.languageId === _.language_id).result)

  def findAllPagination(limit: Int, offset: Int, isPublic: Boolean) = {
    db.run(films.filter(_.public === isPublic)
      .joinLeft(languages).on(_.languageId === _.language_id).take(limit).drop(offset).result)
  }

  def findAll() = {
    db.run(films.result)
  }

  def deleteById(id: Int) = {
    db.run(films.delete) map {
      _ > 0
    }
  }

  def findAllByNameDate(name: String, date: String): Future[Seq[(Film, Option[Language])]] = {
    db.run(films.filter(data => (data.name.startsWith(name) && data.releaseDate === date))
      .joinLeft(languages).on(_.languageId === _.language_id).result)
  }

  def findAllByDate(date: String): Future[Seq[(Film, Option[Language])]] = {
    db.run(films.filter(data => data.releaseDate === date)
      .joinLeft(languages).on(_.languageId === _.language_id).result)
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

  def findAllByName(name: String) = {
    db.run(films.filter(_.name.startsWith(name)).joinLeft(languages).on(_.languageId === _.language_id).result)
  }

  def findAllByYear(releaseDate: String) = {
    db.run(films.filter(_.releaseDate === releaseDate).joinLeft(languages).on(_.languageId === _.language_id).result)
  }

  def findAllById(id: Int): Future[Seq[(Film, Option[Language])]] = {
    db.run(films.filter(_.filmId === id).joinLeft(languages).on(_.languageId === _.language_id).result)
  }

  def deleteAll(): Future[Int] = {
    db.run(films.delete)
  }

  def getRecommendedFilms(id: Int) = {
    val minNumOfPoints: Byte = 5
    val action = comments.filter(_.rating >= minNumOfPoints).filter(comment => (comment.recommendedFilm1 === id
      || comment.recommendedFilm2 === id || comment.recommendedFilm3 === id
      || comment.recommendedFilm4 === id || comment.recommendedFilm5 === id))
      .result

    val result2 = db.run(action)

    val result = db.run(action).map(_.map(data => {
      List(Option(data.filmId), data.recommendedFilm1, data.recommendedFilm2, data.recommendedFilm3, data.recommendedFilm4, data.recommendedFilm5)
    })).map(_.flatMap(_.filter(_.nonEmpty))).map(_.map(_.get))

    result.map(_.distinct).zip(result).map(num => num._1.map(info => (info -> num._2.count(_ == info)))).map(_.sortBy(_._2).reverse)
  }


  override def insertList(entities: Seq[Film]): Future[Seq[Option[Film]]] = ???
}