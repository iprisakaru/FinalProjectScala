package by.bsu.model.repository

import by.bsu.model.Db

case class DirectorFilm(directorFilmId: Option[Int], directorId: Int, filmId: Int)

trait DirectorsFilmsTable extends DirectorsTable with FilmsTable {
  this: Db =>

  import config.driver.api._

  class DirectorsFilms(tag: Tag) extends Table[DirectorFilm](tag, "directors_films") {

    def director_film_id = column[Option[Int]]("director_film_id", O.PrimaryKey, O.AutoInc)

    def director_id = column[Int]("director_id", O.PrimaryKey)

    def film_id = column[Int]("film_id", O.PrimaryKey)

    def fk_actor_id = foreignKey("fk_actor_id", director_id, directors)(_.director_id)

    def fk_film_id = foreignKey("fk_film_id", film_id, films)(_.filmId)

    def * = (director_film_id, director_id, film_id) <> (DirectorFilm.tupled, DirectorFilm.unapply)
  }

  val directorsFilms = TableQuery[DirectorsFilms]

}
