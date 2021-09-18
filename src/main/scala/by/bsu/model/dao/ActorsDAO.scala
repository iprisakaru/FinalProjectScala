package by.bsu.model.dao

import by.bsu.model.repository.{Actor, ActorsTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class ActorsDAO(val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with ActorsTable {

  override type T = Actor

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def update(id: Int, actor: Actor): Future[Int] = {
    LOGGER.debug(s"Updating actor $id id")
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


  def insert(actor: Actor): Future[Option[Actor]] = {
    LOGGER.debug(s"Inserting actor ${actor.name}")
    val result = db.run(((actors returning actors) += actor).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(actor.name)
    }).flatten
  }

  def insertList(entities: Seq[Actor]): Future[Seq[Option[Actor]]] = {
    Future.sequence(entities.map(actor => insert(actor))).map(_.filter(_.nonEmpty).map(data => Option(data.get)))
  }

  def deleteAll(): Future[Int] = {
    db.run(actors.delete)
  }

}