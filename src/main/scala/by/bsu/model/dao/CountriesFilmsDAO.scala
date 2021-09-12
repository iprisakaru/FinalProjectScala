package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{CountriesFilmsTable, CountriesTable, CountryFilm, FilmsTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

class CountriesFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with CountriesFilmsTable with CountriesTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(countryFilm: CountryFilm): Future[Long] = {
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
    db.run(countriesFilms.filter(data=>(data.country_id === countryId && data.film_id===filmId)).result.headOption.map(_.get.countryFilmId))
  }

  def insertUniq(countryFilm: CountryFilm): Future[Long] = {
    db.run(countriesFilms.filter(data=>(data.country_id === countryFilm.countryId && data.film_id===countryFilm.filmId)).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(countryFilm.countryId, countryFilm.filmId).map(_.get)
      else insert(countryFilm)}).flatten
  }
}
