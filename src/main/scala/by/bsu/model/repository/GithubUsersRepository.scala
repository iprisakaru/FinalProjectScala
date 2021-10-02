package by.bsu.model.repository

import by.bsu.model.Db

case class GithubUser(id: Option[Int], login: String, nodeId: String)

trait GithubUsersTable {
  this: Db =>
  import config.driver.api._

  class GithubUsers(tag: Tag) extends Table[GithubUser](tag, "github_users") {
    def githubUserId = column[Int]("github_user_id", O.PrimaryKey, O.AutoInc)

    def login = column[String]("login")

    def nodeId = column[String]("node_id")

    def * = (githubUserId.?, login, nodeId) <> (GithubUser.tupled, GithubUser.unapply)
  }

  val githubUsers = TableQuery[GithubUsers]
}
