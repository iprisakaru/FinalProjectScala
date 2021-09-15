package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Genre, Language, LanguagesTable}
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.util.Try

class LanguagesDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with LanguagesTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def update(id: Int, language: Language): Future[Int] = {
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

  def insertUniq(language: Language): Future[Option[Language]] = {
    db.run(createQuery(language).asTry).map(_.toOption)
  }

  def deleteAll(): Future[Int] = {
    db.run(languages.delete)
  }

  private def createQuery(entity: Language): DBIOAction[Language, NoStream, Effect.Read with Effect.Write with Effect.Transactional] =
    (for {
      existing <- languages.filter(_.name === entity.name).result //Check, if entity exists
      data <- if (existing.isEmpty)
        (languages returning languages) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield data).transactionally


  def insertListLanguages(entities: Seq[Language]) = {
    db.run(DBIO.sequence(entities.map(createQuery(_))).transactionally.asTry).map(_.toOption)
  }


}