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

  def insert(actorFilm: DirectorFilm): Future[DirectorFilm] = {
    db.run((directorsFilms returning directorsFilms.map(_.director_film_id) += actorFilm))
      .map(id => actorFilm.copy(id))
  }

  def insertIfUniq(actorName: String, filmName: String, releaseDate: String): Either[String, Future[DirectorFilm]] = {

    val tryingToInsert = Try(db.run(directors.joinFull(films).joinFull(directorsFilms).result.map(data => {
      (data.map(_._1.get._1).filter(data => data.get.name == actorName).head.get.id.get,
        data.map(_._1.get._2).filter(data => ((data.get.name == filmName) && (data.get.release_date == releaseDate))).head.get.id.get
      )
    }))).map(_.map(data => insert(DirectorFilm(None, data._1, data._2))).flatten)

    if (tryingToInsert.isSuccess) Right(tryingToInsert.toOption.get)
    else {
      Left(new Exception + s"Foreign keys for trip of user $actorName are impossible to find")
    }

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
