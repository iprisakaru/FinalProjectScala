package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{CountriesTable, Country}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class CountriesDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with CountriesTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def update(id: Int, country: Country): Future[Int] = {
    LOGGER.debug(s"Updating country $id id")
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

  def insertUniq(entity: Country) = {
    LOGGER.debug(s"Inserting admin ${entity.name}")
    val result = db.run(((countries returning countries) += entity).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(entity.name)
    }).flatten

  }

  def insertListCountry(entities: Seq[Country]) = {
    Future.sequence(entities.map(entity => insertUniq(entity))).map(_.filter(_.nonEmpty).map(data => Option(data.get)))
  }

  def deleteAll(): Future[Int] = {
    db.run(countries.delete)
  }
}