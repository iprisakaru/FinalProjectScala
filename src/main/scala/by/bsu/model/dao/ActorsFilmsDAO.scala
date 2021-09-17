package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{ActorFilm, ActorsFilmsTable, ActorsTable, FilmsTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

class ActorsFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with ActorsFilmsTable with ActorsTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def insertActorFilm(actorFilm: ActorFilm): Future[ActorFilm] = {
    db.run((actorsFilms returning actorsFilms.map(_.actor_film_id) += actorFilm))
      .map(id => actorFilm.copy(id))
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
    db.run(actorsFilms.filter(data => (data.actor_id === actorId && data.film_id === filmId)).result.headOption.map(_.get.actorFilmId))
  }

  def findAll() = {
    db.run(actorsFilms.result)
  }

  def deleteByFilmIdQuery(id: Long) = {
    actorsFilms.filter(e => e.film_id === id).delete
  }


  def insertListActorFilm(entities: Seq[ActorFilm]) = {
    db.run(DBIO.sequence(entities.map(entity => (actorsFilms returning actorsFilms) += entity)).asTry).map(_.toOption)
  }
}
