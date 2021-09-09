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

  def insert(actorFilm: ActorFilm): Future[ActorFilm] = {
    db.run((actorsFilms returning actorsFilms.map(_.actor_film_id) += actorFilm))
      .map(id => actorFilm.copy(id))
  }

  def insertIfUniq(actorName: String, filmName: String, releaseDate: String): Either[String, Future[ActorFilm]] = {

    val tryingToInsert = Try(db.run(actors.joinFull(films).joinFull(actorsFilms).result.map(data => {
      (data.map(_._1.get._1).filter(data => data.get.name == actorName).head.get.id.get,
        data.map(_._1.get._2).filter(data => ((data.get.name == filmName) && (data.get.release_date == releaseDate))).head.get.id.get
      )
    }))).map(_.map(data => insert(ActorFilm(None, data._1, data._2))).flatten)

    if (tryingToInsert.isSuccess) Right(tryingToInsert.toOption.get)
    else {
      Left(new Exception + s"Foreign keys for trip of user $actorName are impossible to find")
    }

  }

  def deleteById(actorId: Int, filmId: Long): Future[Boolean] = {
    db.run(actorsFilms.filter(data => (data.actor_id === actorId) && (data.film_id === filmId)).delete) map {
      _ > 0
    }
  }

  def deleteAll(): Future[Int] = {
    db.run(actorsFilms.delete)
  }
}
