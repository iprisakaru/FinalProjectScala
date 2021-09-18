package by.bsu.model.repository

import by.bsu.model.Db

case class Admin(id: Option[Int], username: String, password: String)

trait AdminsTable {
  this: Db =>

  import config.driver.api._

  class Admins(tag: Tag) extends Table[Admin](tag, "admins") {
    def admin_id = column[Int]("admin_id", O.PrimaryKey, O.AutoInc)

    def username = column[String]("username")

    def password = column[String]("password")

    def * = (admin_id.?, username, password) <> (Admin.tupled, Admin.unapply)
  }

  val admins = TableQuery[Admins]
}
