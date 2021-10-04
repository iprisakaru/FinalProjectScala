package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{User, UsersTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class UsersDAO(val config: DatabaseConfig[JdbcProfile])
  extends UsersTable with Db {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)


  def insertGithub(user: User): Future[Option[User]] = {
    LOGGER.debug(s"Inserting user ${user.githubLogin}")
    val result = db.run(((users returning users) += user).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByGhId(user.githubNodeId.get)
    }).flatten
  }

  def insertGoogle(user: User): Future[Option[User]] = {
    LOGGER.debug(s"Inserting user ${user.githubLogin}")
    val result = db.run(((users returning users) += user).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByGglId(user.gglId.get)
    }).flatten
  }


  def updateGithub(id: Int, user: User): Future[Int] = {
    LOGGER.debug(s"Updating user $id id")
    db.run(users.filter(_.userId === id).map(customer => (customer.login))
      .update(user.githubLogin))
  }

  def findAll(): Future[Seq[User]] = db.run(users.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(users.filter(_.userId === id).delete) map {
      _ > 0
    }
  }


  def findById(id: Int): Future[Option[User]] = {
    db.run(users.filter(_.userId === id).result.headOption)
  }

  def findByGhId(id: String): Future[Option[User]] = {
    db.run(users.filter(_.githubNodeId === id).result.headOption)
  }

  def findByGglId(id: String): Future[Option[User]] = {
    db.run(users.filter(_.githubId === id).result.headOption)
  }

  def deleteAll(): Future[Int] = {
    db.run(users.delete)
  }

}
