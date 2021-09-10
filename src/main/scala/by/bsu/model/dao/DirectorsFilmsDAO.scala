package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{DirectorFilm, DirectorsFilmsTable, DirectorsTable, FilmsTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

class DirectorsFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with DirectorsFilmsTable with DirectorsTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(actorFilm: DirectorFilm): Future[Long] = {
    db.run((directorsFilms returning directorsFilms.map(_.director_film_id) += actorFilm))
      .map(id => actorFilm.copy(id)).map(_.directorFilmId.get)
  }


  def deleteById(directorId: Int, filmId: Long): Future[Boolean] = {
    db.run(directorsFilms.filter(data => (data.director_id === directorId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteAll(): Future[Int] = {
    db.run(directorsFilms.delete)
  }
}
