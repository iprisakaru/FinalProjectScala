package by.bsu.model.repository

import by.bsu.model.Db

case class GenreFilm(genreFilmId: Option[Int], genreId: Int, filmId: Int)

trait GenresFilmsTable extends GenresTable with FilmsTable {
  this: Db =>

  import config.driver.api._

  class GenresFilms(tag: Tag) extends Table[GenreFilm](tag, "genres_films") {

    def genre_film_id = column[Option[Int]]("genre_film_id", O.PrimaryKey, O.AutoInc)

    def genre_id = column[Int]("genre_id", O.PrimaryKey)

    def film_id = column[Int]("film_id", O.PrimaryKey)

    def fk_genre_id = foreignKey("fk_genre_id", genre_id, genres)(_.genre_id)

    def fk_film_id = foreignKey("fk_film_id", film_id, films)(_.filmId)

    def * = (genre_film_id, genre_id, film_id) <> (GenreFilm.tupled, GenreFilm.unapply)
  }

  val genresFilms = TableQuery[GenresFilms]

}
