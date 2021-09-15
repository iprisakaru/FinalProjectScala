package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Actor, ActorsTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class ActorsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with ActorsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def update(id: Int, actor: Actor): Future[Int] = {
    db.run(actors.filter(_.actor_id === id).map(customer => (customer.name))
      .update(actor.name))
  }

  def findAll(): Future[Seq[Actor]] = db.run(actors.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(actors.filter(_.actor_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[Actor]] = {
    db.run(actors.filter(_.actor_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Actor]] = {
    db.run(actors.filter(_.name === name).result.headOption)
  }

  def insertUniq(actor: Actor): Future[Option[Actor]] = {
    db.run(createQuery(actor).asTry).map(_.toOption)
  }

  def deleteAll(): Future[Int] = {
    db.run(actors.delete)
  }

  private def createQuery(entity: Actor): DBIOAction[Actor, NoStream, Effect.Read with Effect.Write with Effect.Transactional] =

    (for {
      existing <- actors.filter(_.name === entity.name).result //Check, if entity exists
      data <- if (existing.isEmpty)
        (actors returning actors) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield data).transactionally


  def insertListActor(entities: Seq[Actor]): Future[Option[Seq[Actor]]] = {
    db.run(DBIO.sequence(entities.map(createQuery(_))).transactionally.asTry).map(_.toOption)
  }
}