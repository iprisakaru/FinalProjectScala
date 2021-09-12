package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Actor, ActorsTable}
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class ActorsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with ActorsTable with HelpFunctions {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(actor: Actor): Future[Actor] = {
    db.run(actors returning actors.map(_.actor_id) += actor)
      .map(id => actor.copy(id = Option(id)))
  }


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

  def insertUniq(actor: Actor): Future[Actor] = {
    db.run(actors.filter(_.name === actor.name).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) findByName(actor.name).map(_.get)
      else insert(actor)}).flatten
  }

  def deleteAll(): Future[Int] = {
    db.run(actors.delete)
  }
}