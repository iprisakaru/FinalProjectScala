package by.bsu.model.dao

import by.bsu.model.repository.{CountriesTable, Country}
import by.bsu.model.Db
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.util.Try

class CountriesDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with CountriesTable  {

  import config.driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

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

  def insertUniq(country: Country): Future[Option[Country]] = {
    db.run(createQuery(country).asTry).map(_.toOption)
  }

  private def createQuery(entity: Country): DBIOAction[Country, NoStream, Effect.Read with Effect.Write with Effect.Transactional] =

    (for {
      existing <- countries.filter(e => e.name === entity.name).result //Check, if entity exists
      e <- if (existing.isEmpty)
        (countries returning countries) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield e).transactionally


  def insertListCountries(entities: Seq[Country])= {
    db.run(DBIO.sequence(entities.map(createQuery(_))).transactionally.asTry).map(_.toOption)
  }

  def deleteAll(): Future[Int] = {
    db.run(countries.delete)
  }
}