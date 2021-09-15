package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{CountriesFilmsTable, CountriesTable, CountryFilm, FilmsTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class CountriesFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with CountriesFilmsTable with CountriesTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insertCountryFilm(countryFilm: CountryFilm): Future[Long] = {
    db.run((countriesFilms returning countriesFilms.map(_.country_film_id) += countryFilm))
      .map(id => countryFilm.copy(id)).map(_.countryFilmId.get)
  }


  def deleteById(countryId: Int, filmId: Long): Future[Boolean] = {
    db.run(countriesFilms.filter(data => (data.country_id === countryId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteAll(): Future[Int] = {
    db.run(countriesFilms.delete)
  }

  def findByName(countryId: Int, filmId: Long): Future[Option[Long]] = {
    db.run(countriesFilms.filter(data => (data.country_id === countryId && data.film_id === filmId)).result.headOption.map(_.get.countryFilmId))
  }

  def insertUniq(countryFilm: CountryFilm): Future[Long] = {
    db.run(countriesFilms.filter(data => (data.country_id === countryFilm.countryId && data.film_id === countryFilm.filmId)).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(countryFilm.countryId, countryFilm.filmId).map(_.get)
      else insertCountryFilm(countryFilm)
    }).flatten
  }

  def deleteByFilmIdQuery(id: Long) = {
    countriesFilms.filter(e => e.film_id === id).delete
  }

  private def createQuery(entity: CountryFilm): DBIOAction[CountryFilm, NoStream, Effect.Read with Effect.Write with Effect.Transactional] =

    (for {
      existing <- countriesFilms.filter(data => data.country_id === entity.countryId && data.film_id === entity.filmId).result //Check, if entity exists
      data <- if (existing.isEmpty)
        (countriesFilms returning countriesFilms) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield data).transactionally

  def findAll(): Future[Seq[CountryFilm]] = db.run(countriesFilms.result)


  def insertListCountryFilm(entities: Seq[CountryFilm]) = {
    db.run(DBIO.sequence(entities.map(createQuery(_))).transactionally.asTry).map(_.toOption)
  }
}
