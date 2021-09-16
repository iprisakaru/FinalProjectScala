package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{DirectorFilm, DirectorsFilmsTable, DirectorsTable, FilmsTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class DirectorsFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with DirectorsFilmsTable with DirectorsTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def findAll(): Future[Seq[DirectorFilm]] = db.run(directorsFilms.result)


  def insertDirectorFilm(actorFilm: DirectorFilm): Future[Long] = {
    db.run((directorsFilms returning directorsFilms.map(_.director_film_id) += actorFilm))
      .map(id => actorFilm.copy(id)).map(_.directorFilmId.get)
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

  def insertUniq(directorFilm: DirectorFilm): Future[Long] = {
    db.run(directorsFilms.filter(data => (data.director_id === directorFilm.directorId && data.film_id === directorFilm.filmId)).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(directorFilm.directorId, directorFilm.filmId).map(_.get)
      else insertDirectorFilm(directorFilm)
    }).flatten
  }

  private def createQuery(entity: DirectorFilm): DBIOAction[DirectorFilm, NoStream, Effect.Read with Effect.Write with Effect.Transactional] =

    (for {
      existing <- directorsFilms.filter(data => data.director_id === entity.directorId && data.film_id === entity.filmId).result //Check, if entity exists
      data <- if (existing.isEmpty)
        (directorsFilms returning directorsFilms) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield data).transactionally


  def insertListDirectorsFilm(entities: Seq[DirectorFilm]) = {
    db.run(DBIO.sequence(entities.map(createQuery(_))).transactionally.asTry).map(_.toOption)
  }
}
