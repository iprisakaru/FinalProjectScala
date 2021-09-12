package by.bsu.model.dao

import by.bsu.model.repository.{Actor, ActorsTable, Language, LanguagesTable}
import by.bsu.model.Db
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class LanguagesDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with LanguagesTable with HelpFunctions {

  import config.driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(language: Language): Future[Language] = {
    db.run(languages returning languages.map(_.language_id) += language)
      .map(id => language.copy(id = Option(id)))
  }


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

  def insertUniq(language: Language): Future[Language] = {
    db.run(languages.filter(_.name === language.name).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(language.name).map(_.get)
      else insert(language)}).flatten
  }

  def deleteAll(): Future[Int] = {
    db.run(languages.delete)
  }


}