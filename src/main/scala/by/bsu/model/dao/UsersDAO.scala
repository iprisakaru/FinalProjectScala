package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{User, UsersTable}
import by.bsu.utils.HelpFunctions
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class UsersDAO(val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with UsersTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  override type T = User

  def insert(user: User): Future[Option[User]] = {
    LOGGER.debug(s"Inserting user ${user.code}")
    val result = db.run(((users returning users) += user).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(user.code)
    }).flatten
  }


  def update(id: Int, user: User): Future[Int] = {
    LOGGER.debug(s"Updating user $id id")
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


  def deleteAll(): Future[Int] = {
    db.run(users.delete)
  }

  override def insertList(entities: Seq[User]): Future[Seq[Option[User]]] = ???
}
