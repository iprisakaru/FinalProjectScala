package by.bsu.model.dao

import by.bsu.model.repository.{Language, LanguagesTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class LanguagesDAO(val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with LanguagesTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  override type T = Language

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def update(id: Int, language: Language): Future[Int] = {
    LOGGER.debug(s"Updating language $id id")
    db.run(languages.filter(_.language_id === id).map(customer => (customer.name))
      .update(language.name))
  }

  def findAll(): Future[Seq[Language]] = db.run(languages.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(languages.filter(_.language_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[Language]] = {
    db.run(languages.filter(_.language_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Language]] = {
    db.run(languages.filter(_.name === name).result.headOption)
  }

  def insert(entity: Language): Future[Option[Language]] = {
    LOGGER.debug(s"Inserting admin ${entity.name}")
    val result = db.run(((languages returning languages) += entity).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(entity.name)
    }).flatten

  }

  def insertList(entities: Seq[Language]) = {
    Future.sequence(entities.map(entity => insert(entity))).map(_.filter(_.nonEmpty).map(data => Option(data.get)))
  }

  def deleteAll(): Future[Int] = {
    db.run(languages.delete)
  }


}