package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{CountriesFilmsTable, CountriesTable, CountryFilm, FilmsTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class CountriesFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with CountriesFilmsTable with CountriesTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def insertCountryFilm(countryFilm: CountryFilm): Future[Int] = {
    db.run((countriesFilms returning countriesFilms.map(_.country_film_id) += countryFilm))
      .map(id => countryFilm.copy(id)).map(_.countryFilmId.get)
  }


  def deleteById(countryId: Int, filmId: Int): Future[Boolean] = {
    db.run(countriesFilms.filter(data => (data.country_id === countryId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteAll(): Future[Int] = {
    db.run(countriesFilms.delete)
  }

  def findByName(countryId: Int, filmId: Int): Future[Option[Int]] = {
    db.run(countriesFilms.filter(data => (data.country_id === countryId && data.film_id === filmId)).result.headOption.map(_.get.countryFilmId))
  }

  def insertUniq(countryFilm: CountryFilm): Future[Int] = {
    db.run(countriesFilms.filter(data => (data.country_id === countryFilm.countryId && data.film_id === countryFilm.filmId)).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(countryFilm.countryId, countryFilm.filmId).map(_.get)
      else insertCountryFilm(countryFilm)
    }).flatten
  }

  def deleteByFilmIdQuery(id: Int) = {
    countriesFilms.filter(e => e.film_id === id).delete
  }

  def findAll(): Future[Seq[CountryFilm]] = db.run(countriesFilms.result)


  def insertListCountryFilm(entities: Seq[CountryFilm]) = {
    db.run(DBIO.sequence(entities.map(entity =>
      ((countriesFilms returning countriesFilms) += entity))).transactionally.asTry).map(_.toOption)
  }

  def joinCountriesToFilmsId() = {
    db.run(countriesFilms.joinLeft(countries).on(_.country_id === _.country_id).result)
      .map(_.groupBy(_._1.filmId))
  }

  def joinCountryToFilmId(id: Int) = {
    db.run(countriesFilms.filter(_.film_id === id).joinLeft(countries).on(_.country_id===_.country_id).result)
      .map(_.groupBy(_._1.filmId))
  }
}
