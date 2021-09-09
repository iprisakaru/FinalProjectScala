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

  def insert(countryFilm: CountryFilm): Future[CountryFilm] = {
    db.run((countriesFilms returning countriesFilms.map(_.country_film_id) += countryFilm))
      .map(id => countryFilm.copy(id))
  }

  def insertIfUniq(countryName: String, filmName: String, releaseDate: String): Either[String, Future[CountryFilm]] = {

    val tryingToInsert = Try(db.run(countries.joinFull(films).joinFull(countriesFilms).result.map(data => {
      (data.map(_._1.get._1).filter(data => data.get.name == countryName).head.get.id.get,
        data.map(_._1.get._2).filter(data => ((data.get.name == filmName) && (data.get.release_date == releaseDate))).head.get.id.get
      )
    }))).map(_.map(data => insert(CountryFilm(None, data._1, data._2))).flatten)

    if (tryingToInsert.isSuccess) Right(tryingToInsert.toOption.get)
    else {
      Left(new Exception + s"Foreign keys for trip of user $countryName are impossible to find")
    }

  }

  def deleteById(countryId: Int, filmId: Long): Future[Boolean] = {
    db.run(countriesFilms.filter(data => (data.country_id === countryId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteAll(): Future[Int] = {
    db.run(countriesFilms.delete)
  }
}
