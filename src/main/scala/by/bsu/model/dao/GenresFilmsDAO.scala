package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{FilmsTable, GenreFilm, GenresFilmsTable, GenresTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

class GenresFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with GenresFilmsTable with GenresTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(actorFilm: GenreFilm): Future[GenreFilm] = {
    db.run((genresFilms returning genresFilms.map(_.genre_film_id) += actorFilm))
      .map(id => actorFilm.copy(id))
  }

  def insertIfUniq(actorName: String, filmName: String, releaseDate: String): Either[String, Future[GenreFilm]] = {

    val tryingToInsert = Try(db.run(genres.joinFull(films).joinFull(genresFilms).result.map(data => {
      (data.map(_._1.get._1).filter(data => data.get.name == actorName).head.get.id.get,
        data.map(_._1.get._2).filter(data => ((data.get.name == filmName) && (data.get.release_date == releaseDate))).head.get.id.get
      )
    }))).map(_.map(data => insert(GenreFilm(None, data._1, data._2))).flatten)

    if (tryingToInsert.isSuccess) Right(tryingToInsert.toOption.get)
    else {
      Left(new Exception + s"Foreign keys for trip of user $actorName are impossible to find")
    }

  }

  def deleteById(genreId: Int, filmId: Long): Future[Boolean] = {
    db.run(genresFilms.filter(data => (data.genre_id === genreId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteAll(): Future[Int] = {
    db.run(genresFilms.delete)
  }
}
