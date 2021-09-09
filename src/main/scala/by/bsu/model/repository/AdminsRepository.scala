package by.bsu.model.repository

import by.bsu.model.Db

case class Admin(id: Option[Int], code: String)

trait AdminsTable {
  this: Db =>

  import config.driver.api._

  class Admins(tag: Tag) extends Table[Admin](tag, "admins") {
    def admin_id = column[Int]("actor_id", O.PrimaryKey, O.AutoInc)

    def code = column[String]("code")

    def * = (admin_id.?, code) <> (Admin.tupled, Admin.unapply)
  }

  val admins = TableQuery[Admins]
}
