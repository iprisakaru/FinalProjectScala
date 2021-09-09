package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Genre, GenresTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class GenresDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with GenresTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(actor: Genre): Future[Genre] = {
    db.run(genres returning genres.map(_.genre_id) += actor)
      .map(id => actor.copy(id = Option(id)))
  }


  def update(id: Int, genre: Genre): Future[Int] = {
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

  def insertUniq(genre: Genre): Future[Either[String, Future[Genre]]] = {
    db.run(genres.filter(_.name === genre.name).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) Left(new Exception + s" ${genre.name} is already exist in database.")
      else Right(insert(genre))
    })

  }

  def deleteAll(): Future[Int] = {
    db.run(genres.delete)
  }
}