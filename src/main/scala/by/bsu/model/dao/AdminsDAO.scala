package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{Admin, AdminsTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class AdminsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with AdminsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(actor: Admin): Future[Admin] = {
    db.run(admins returning admins.map(_.admin_id) += actor)
      .map(id => actor.copy(id = Option(id)))
  }


  def update(id: Int, actor: Admin): Future[Int] = {
    db.run(admins.filter(_.admin_id === id).map(customer => (customer.code))
      .update(actor.code))
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
    db.run(admins.filter(_.code === name).result.headOption)
  }

  def insertUniq(actor: Admin): Future[Either[String, Future[Option[Int]]]] = {
    db.run(admins.filter(_.code === actor.code).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) Left(new Exception + s" ${actor.code} is already exist in database.")
      else Right(insert(actor).map(_.id))
    })

  }

  def deleteAll(): Future[Int] = {
    db.run(admins.delete)
  }
}