package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{ActorFilm, ActorsFilmsTable, ActorsTable, FilmsTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class ActorsFilmsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with ActorsFilmsTable with ActorsTable with FilmsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

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

  def deleteByFilmIdQuery(id: Long)={
    actorsFilms.filter(e => e.film_id === id).delete
  }

  private def createQuery(entity: ActorFilm): DBIOAction[ActorFilm, NoStream, Effect.Read with Effect.Write with Effect.Transactional] =

    (for {
      existing <- actorsFilms.filter(e => e.actor_id === entity.actorId && e.film_id === entity.filmId).result //Check, if entity exists
      e <- if (existing.isEmpty)
        (actorsFilms returning actorsFilms) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield e).transactionally


  def insertListActorFilm(entities: Seq[ActorFilm]) = {
    db.run(DBIO.sequence(entities.map(createQuery(_))).transactionally.asTry).map(_.toOption)
  }
}
