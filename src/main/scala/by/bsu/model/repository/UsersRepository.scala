package by.bsu.model.repository

import by.bsu.model.Db

case class User(id: Option[Int], githubLogin: String, githubNodeId: Option[String],
                gglId: Option[String])

trait UsersTable {
  this: Db =>

  import config.driver.api._

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def userId = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

    def login = column[String]("login") // login is email from Google API or GitHub login

    def githubNodeId = column[String]("gh_node_id")

    def githubId = column[String]("ggl_id")

    def * = (userId.?, login, githubNodeId.?, githubId.?) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]
}
