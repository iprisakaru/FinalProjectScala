package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Admin, AdminsTable}
import by.bsu.utils.HelpFunctions
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class AdminsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with AdminsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insertUniq(director: Admin): Future[Option[Admin]] = {
    val e =db.run(createQuery(director).asTry).map(_.toOption)
    Await.result(e, 1000 seconds)
    e
  }

  def insert(admin: Admin): Future[Admin] = {
    val e = db.run(admins returning admins.map(_.admin_id) += admin)
      .map(id => admin.copy(id = Option(id)))
    Await.result(e, 1000 seconds)
    e
  }

  private def createQuery(entity: Admin): DBIOAction[Admin, NoStream, Effect.Read with Effect.Write with Effect.Transactional] = {
    (for {
      existing <- admins.filter(e => e.username === entity.username).result //Check, if entity exists
      e <- if (existing.isEmpty)
        (admins returning admins) += entity
      else {
        throw new Exception(s"Create failed: entity already exists")
      }
    } yield (e)).transactionally

  }

  def getPassword(username: String): Future[Admin] = {
    db.run(admins.filter(data => (data.username === username)).result.head)
  }


  def update(id: Int, actor: Admin): Future[Int] = {
    db.run(admins.filter(_.admin_id === id).map(customer => (customer.username))
      .update(actor.username))
  }

  def findAll(): Future[Seq[Admin]] = db.run(admins.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(admins.filter(_.admin_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[Admin]] = {
    db.run(admins.filter(_.admin_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Admin]] = {
    db.run(admins.filter(_.username === name).result.headOption)
  }


  def deleteAll(): Future[Int] = {
    db.run(admins.delete)
  }
}