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
  extends BaseDAO with DirectorsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  override type T = Director

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

  def insert(entity: Director) = {
    LOGGER.debug(s"Inserting admin ${entity.name}")
    val result = db.run(((directors returning directors) += entity).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(entity.name)
    }).flatten

  }

  def insertList(entities: Seq[Director]) = {
    Future.sequence(entities.map(entity => insert(entity))).map(_.filter(_.nonEmpty).map(data => Option(data.get)))
  }
}