package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{FilmsTable, GenreFilm, GenresFilmsTable, GenresTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class GenresFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with GenresFilmsTable with GenresTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def insertListGenresFilm(entities: Seq[GenreFilm]) = {
    db.run(DBIO.sequence(entities.map(entity => (genresFilms returning genresFilms) += entity)).asTry).map(_.toOption)
  }

  def deleteById(genreId: Int, filmId: Int): Future[Boolean] = {
    db.run(genresFilms.filter(data => (data.genre_id === genreId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def findAll(): Future[Seq[GenreFilm]] = db.run(genresFilms.result)


  def deleteAll(): Future[Int] = {
    db.run(genresFilms.delete)
  }

  def deleteByFilmIdQuery(id: Int) = {
    genresFilms.filter(e => e.film_id === id).delete
  }

  def findByName(genreId: Int, filmId: Int): Future[Option[Int]] = {
    db.run(genresFilms.filter(data => (data.genre_id === genreId && data.film_id === filmId)).result.headOption.map(_.get.genreFilmId))
  }

  def joinGenresToFilmsId() = {
    db.run(genresFilms.joinLeft(genres).on(_.genre_id === _.genre_id).result)
      .map(_.groupBy(_._1.filmId))
  }

}
