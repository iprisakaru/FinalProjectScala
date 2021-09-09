package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{User, UsersTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class UsersDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with UsersTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(user: User): Future[User] = {
    db.run(users returning users.map(_.user_id) += user)
      .map(id => user.copy(id = Option(id)))
  }


  def update(id: Int, user: User): Future[Int] = {
    db.run(users.filter(_.user_id === id).map(customer => (customer.code))
      .update(user.code))
  }

  def findAll(): Future[Seq[User]] = db.run(users.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(users.filter(_.user_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[User]] = {
    db.run(users.filter(_.user_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[User]] = {
    db.run(users.filter(_.code === name).result.headOption)
  }

  def insertUniq(user: User): Future[Either[String, Future[Option[Int]]]] = {
    db.run(users.filter(_.code === user.code).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) Left(new Exception + s" ${user.code} is already exist in database.")
      else Right(insert(user).map(_.id))
    })

  }

  def deleteAll(): Future[Int] = {
    db.run(users.delete)
  }
}
