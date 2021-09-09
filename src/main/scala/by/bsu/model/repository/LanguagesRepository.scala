package by.bsu.model.repository

import by.bsu.model.Db

case class Language(id: Option[Int], name: String)


trait LanguagesTable {
  this: Db =>

  import config.driver.api._

  class Languages(tag: Tag) extends Table[Language](tag, "languages") {
    def language_id = column[Int]("language_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (language_id.?, name) <> (Language.tupled, Language.unapply)
  }

  val languages = TableQuery[Languages]
}
