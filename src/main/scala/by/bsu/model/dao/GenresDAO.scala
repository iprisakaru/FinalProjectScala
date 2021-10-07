package by.bsu.model.dao

import by.bsu.model.repository._
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class GenresDAO(val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with GenresTable with CommentsTable with FilmsTable with GenresFilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  override type T = Genre

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def update(id: Int, genre: Genre): Future[Int] = {
    LOGGER.debug(s"Updating genre $id id")
    db.run(genres.filter(_.genre_id === id).map(customer => (customer.name))
      .update(genre.name))
  }

  def findAll(): Future[Seq[Genre]] = db.run(genres.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(genres.filter(_.genre_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[Genre]] = {
    db.run(genres.filter(_.genre_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Genre]] = {
    db.run(genres.filter(_.name === name).result.headOption)
  }

  def insert(entity: Genre): Future[Option[Genre]] = {
    LOGGER.debug(s"Inserting admin ${entity.name}")
    val result = db.run(((genres returning genres) += entity).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(entity.name)
    }).flatten

  }

  def insertList(entities: Seq[Genre]) = {
    Future.sequence(entities.map(entity => insert(entity))).map(_.filter(_.nonEmpty).map(data => Option(data.get)))
  }

  def deleteAll(): Future[Int] = {
    db.run(genres.delete)
  }

  def getTopFilmsByGenre(genreId: Int) = {
    val action = genresFilms.filter(_.genre_id === genreId).joinLeft(films.joinLeft(comments)
      .on(_.filmId === _.filmId)).on(_.film_id === _._1.filmId).result
      .map(_.map(_._2.filter(_._2.nonEmpty)))
    val result = db.run(action)

    result.map(_.groupBy(_.map(_._1))).map(_.filter(_._1.nonEmpty))
      .map(_.map(data => (data._1.get, data._2.filter(_.nonEmpty)
        .map(data => (data.get._1, data.get._2))))).map(_.map(data => (data._1, data._2.map(_._2)
      .filter(_.nonEmpty).map(_.get.rating).sum.toFloat / data._2.size)).toSeq.sortBy(_._2)).map(_.map(data => (data._1.id, data._2)).map(data => (data._1, data._2)))
  }
}