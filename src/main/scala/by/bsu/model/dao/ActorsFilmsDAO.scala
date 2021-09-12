package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{ActorFilm, ActorsFilmsTable, ActorsTable, FilmsTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

class ActorsFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with ActorsFilmsTable with ActorsTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(actorFilm: ActorFilm): Future[Long] = {
    db.run((actorsFilms returning actorsFilms.map(_.actor_film_id) += actorFilm))
      .map(id => actorFilm.copy(id)).map(_.actorFilmId.get)
  }



  def deleteById(actorId: Int, filmId: Long): Future[Boolean] = {
    db.run(actorsFilms.filter(data => (data.actor_id === actorId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteAll(): Future[Int] = {
    db.run(actorsFilms.delete)
  }

  def findByName(actorId: Int, filmId: Long): Future[Option[Long]] = {
    db.run(actorsFilms.filter(data=>(data.actor_id === actorId && data.film_id===filmId)).result.headOption.map(_.get.actorFilmId))
  }

  def insertUniq(genre: ActorFilm): Future[Long] = {
    db.run(actorsFilms.filter(data=>(data.actor_id === genre.actorId && data.film_id===genre.filmId)).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(genre.actorId, genre.filmId).map(_.get)
      else insert(genre)}).flatten
  }
}
