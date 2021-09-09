package by.bsu.model.repository

import by.bsu.model.Db

case class User(id: Option[Int], code: String)

trait UsersTable {
  this: Db =>
  import config.driver.api._

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def user_id = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

    def code = column[String]("code")

    def * = (user_id.?, code) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]
}
