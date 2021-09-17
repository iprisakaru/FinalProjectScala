package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Genre, GenresTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class GenresDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with GenresTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

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

  def insertUniq(entity: Genre) = {
    LOGGER.debug(s"Inserting admin ${entity.name}")
    val result = db.run(((genres returning genres) += entity).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(entity.name)
    }).flatten

  }

  def insertListGenres(entities: Seq[Genre]) = {
    Future.sequence(entities.map(entity => insertUniq(entity))).map(_.filter(_.nonEmpty).map(data => Option(data.get)))
  }

  def deleteAll(): Future[Int] = {
    db.run(genres.delete)
  }
}