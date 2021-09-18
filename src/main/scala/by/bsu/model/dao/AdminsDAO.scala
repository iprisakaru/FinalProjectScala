package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Admin, AdminsTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.language.postfixOps

class AdminsDAO(val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with AdminsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  override type T = Admin

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def insert(entity: Admin): Future[Option[Admin]] = {
    LOGGER.debug(s"Inserting admin ${entity.username}")
    val result = db.run(((admins returning admins) += entity).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(entity.username)
    }).flatten

  }

  def insertListActor(entities: Seq[Admin]): Future[Seq[Option[Admin]]] = {
    Future.sequence(entities.map(entity => insert(entity))).map(_.filter(_.nonEmpty).map(data => Option(data.get)))
  }


  def getPassword(username: String): Future[Admin] = {
    db.run(admins.filter(data => (data.username === username)).result.head)
  }


  def update(id: Int, actor: Admin): Future[Int] = {
    LOGGER.debug(s"Updating admin $id id")
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

  def insertList(entities: Seq[Admin]): Future[Seq[Option[Admin]]] = {
    {
      Future.sequence(entities.map(actor => insert(actor))).map(_.filter(_.nonEmpty).map(data => Option(data.get)))
    }

  }
}