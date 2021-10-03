package by.bsu.model.repository

import by.bsu.model.Db

case class User(id: Option[Int], ghLogin: String, ghNodeId: Option[String],
                gglId: Option[String])

trait UsersTable {
  this: Db =>
  import config.driver.api._

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def userId = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

    def login = column[String]("login")

    def ghNodeId = column[String]("gh_node_id")

    def gglId = column[String]("ggl_id")

    def * = (userId.?, login, ghNodeId.?, gglId.?) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]
}
