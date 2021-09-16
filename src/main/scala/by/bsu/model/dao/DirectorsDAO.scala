package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Director, DirectorsTable}
import by.bsu.utils.HelpFunctions
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.util.Try

class DirectorsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with DirectorsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def update(id: Int, director: Director): Future[Int] = {
    LOGGER.debug(s"Updating director $id id")
    db.run(directors.filter(_.director_id === id).map(customer => (customer.name))
      .update(director.name))
  }

  def findAll(): Future[Seq[Director]] = db.run(directors.result)

  def deleteAll(): Future[Int] = {
    db.run(directors.delete)
  }

  def deleteById(id: Int): Future[Boolean] = {
    db.run(directors.filter(_.director_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[Director]] = {
    db.run(directors.filter(_.director_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Director]] = {
    db.run(directors.filter(_.name === name).result.headOption)
  }

  def insertUniq(director: Director): Future[Option[Director]] = {
    LOGGER.debug(s"Inserting director ${director.name}")
    db.run(createQuery(director).asTry).map(_.toOption)
  }

  private def createQuery(entity: Director): DBIOAction[Director, NoStream, Effect.Read with Effect.Write with Effect.Transactional] = {
    (for {
      existing <- directors.filter(_.name === entity.name).result //Check, if entity exists
      data <- if (existing.isEmpty)
        (directors returning directors) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield (data)).transactionally

  }

  def insertListDirectors(entities: Seq[Director]) = {
    db.run(DBIO.sequence(entities.map(createQuery(_))).transactionally.asTry).map(_.toOption)
  }
}