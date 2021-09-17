package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{DirectorFilm, DirectorsFilmsTable, DirectorsTable, FilmsTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class DirectorsFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with DirectorsFilmsTable with DirectorsTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def findAll(): Future[Seq[DirectorFilm]] = db.run(directorsFilms.result)

  def insertListDirectorFilm(entities: Seq[DirectorFilm]) = {
    db.run(DBIO.sequence(entities.map(entity => (directorsFilms returning directorsFilms) += entity)).asTry).map(_.toOption)
  }


  def deleteById(directorId: Int, filmId: Long): Future[Boolean] = {
    db.run(directorsFilms.filter(data => (data.director_id === directorId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteByFilmIdQuery(id: Long) = {
    directorsFilms.filter(e => e.film_id === id).delete
  }

  def deleteAll(): Future[Int] = {
    db.run(directorsFilms.delete)
  }

  def findByName(directorId: Int, filmId: Long): Future[Option[Long]] = {
    db.run(directorsFilms.filter(data => (data.director_id === directorId && data.film_id === filmId)).result.headOption.map(_.get.directorFilmId))
  }

}
