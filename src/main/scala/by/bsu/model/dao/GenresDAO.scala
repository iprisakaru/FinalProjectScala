package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Genre, GenresTable}
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class GenresDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with GenresTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

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

  def insertUniq(actor: Genre): Future[Option[Genre]] = {
    db.run(createQuery(actor).asTry).map(_.toOption)
  }

  def deleteAll(): Future[Int] = {
    db.run(genres.delete)
  }

  private def createQuery(entity: Genre): DBIOAction[Genre, NoStream, Effect.Read with Effect.Write with Effect.Transactional] =

    (for {
      existing <- genres.filter(_.name === entity.name).result //Check, if entity exists
      data <- if (existing.isEmpty)
        (genres returning genres) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield data).transactionally


  def insertListGenres(entities: Seq[Genre]) = {
    db.run(DBIO.sequence(entities.map(createQuery(_))).transactionally.asTry).map(_.toOption)
  }
}