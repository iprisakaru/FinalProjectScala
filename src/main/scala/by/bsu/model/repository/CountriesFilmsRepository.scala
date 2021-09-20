package by.bsu.model.repository

import by.bsu.model.Db

case class CountryFilm(countryFilmId: Option[Int], countryId: Int, filmId: Int)

trait CountriesFilmsTable extends CountriesTable with FilmsTable {
  this: Db =>

  import config.driver.api._

  class CountriesFilms(tag: Tag) extends Table[CountryFilm](tag, "countries_films") {
    def country_film_id = column[Option[Int]]("country_film_id", O.PrimaryKey, O.AutoInc)

    def country_id = column[Int]("country_id", O.PrimaryKey)

    def film_id = column[Int]("film_id", O.PrimaryKey)

    def fk_country_id = foreignKey("fk_country_id", country_id, countries)(_.country_id)

    def fk_film_id = foreignKey("fk_film_id", film_id, films)(_.filmId)

    def * = (country_film_id, country_id, film_id) <> (CountryFilm.tupled, CountryFilm.unapply)
  }

  val countriesFilms = TableQuery[CountriesFilms]
}
