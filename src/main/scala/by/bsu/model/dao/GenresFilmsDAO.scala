package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{FilmsTable, Genre, GenreFilm, GenresFilmsTable, GenresTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

class GenresFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with GenresFilmsTable with GenresTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(genreFilm: GenreFilm): Future[Long] = {
    db.run((genresFilms returning genresFilms.map(_.genre_film_id) += genreFilm))
      .map(id => genreFilm.copy(id)).map(_.genreFilmId.get)
  }

  def deleteById(genreId: Int, filmId: Long): Future[Boolean] = {
    db.run(genresFilms.filter(data => (data.genre_id === genreId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteAll(): Future[Int] = {
    db.run(genresFilms.delete)
  }

  def findByName(genreId: Int, filmId: Long): Future[Option[Long]] = {
    db.run(genresFilms.filter(data=>(data.genre_id === genreId && data.film_id===filmId)).result.headOption.map(_.get.genreFilmId))
  }

  def insertUniq(genre: GenreFilm): Future[Long] = {
    db.run(genresFilms.filter(data=>(data.genre_id === genre.genreId && data.film_id===genre.filmId)).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(genre.genreId, genre.filmId).map(_.get)
      else insert(genre)}).flatten
  }

}
