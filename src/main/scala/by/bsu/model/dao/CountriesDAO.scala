package by.bsu.model.dao

import by.bsu.model.repository.{CountriesTable, Country}
import by.bsu.model.Db
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class CountriesDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with CountriesTable with HelpFunctions {

  import config.driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(country: Country): Future[Country] = {
    db.run(countries returning countries.map(_.country_id) += country)
      .map(id => country.copy(id = Option(id)))
  }


  def update(id: Int, country: Country): Future[Int] = {
    db.run(countries.filter(_.country_id === id).map(customer => (customer.name))
      .update(country.name))
  }

  def findAll(): Future[Seq[Country]] = db.run(countries.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(countries.filter(_.country_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[Country]] = {
    db.run(countries.filter(_.country_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Country]] = {
    db.run(countries.filter(_.name === name).result.headOption)
  }

  def insertUniq(genre: Country): Future[Country] = {
    db.run(countries.filter(_.name === genre.name).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(genre.name).map(_.get)
      else insert(genre)}).flatten
  }

  def deleteAll(): Future[Int] = {
    db.run(countries.delete)
  }
}