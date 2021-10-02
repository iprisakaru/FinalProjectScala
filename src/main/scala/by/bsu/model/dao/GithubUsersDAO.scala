package by.bsu.model.dao

import by.bsu.model.Db
import by.bsu.model.repository.{GithubUser, GithubUsersTable}
import by.bsu.utils.HelpFunctions
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class GithubUsersDAO(val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with GithubUsersTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val LOGGER = Logger.getLogger(this.getClass.getName)

  override type T = GithubUser

  def insert(user: GithubUser): Future[Option[GithubUser]] = {
    LOGGER.debug(s"Inserting user ${user.login}")
    val result = db.run(((githubUsers returning githubUsers) += user).asTry).map(_.toOption)
    result.map(data => {
      if (data.nonEmpty) Future(data)
      else findByName(user.login)
    }).flatten
  }


  def update(id: Int, user: GithubUser): Future[Int] = {
    LOGGER.debug(s"Updating user $id id")
    db.run(githubUsers.filter(_.githubUserId === id).map(customer => (customer.login))
      .update(user.login))
  }

  def findAll(): Future[Seq[GithubUser]] = db.run(githubUsers.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(githubUsers.filter(_.githubUserId === id).delete) map {
      _ > 0
    }
  }


  def findById(id: Int): Future[Option[GithubUser]] = {
    db.run(githubUsers.filter(_.githubUserId === id).result.headOption)
  }

  def findByName(name: String): Future[Option[GithubUser]] = {
    db.run(githubUsers.filter(_.login === name).result.headOption)
  }


  def deleteAll(): Future[Int] = {
    db.run(githubUsers.delete)
  }

  override def insertList(entities: Seq[GithubUser]): Future[Seq[Option[GithubUser]]] = ???
}
