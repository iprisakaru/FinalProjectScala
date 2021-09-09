package by.bsu.model.repository

import by.bsu.model.Db

case class Director(id: Option[Int], name: String)

trait DirectorsTable {

  this: Db =>

  import config.driver.api._

  class Directors(tag: Tag) extends Table[Director](tag, "directors") {
    def director_id = column[Int]("director_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (director_id.?, name) <> (Director.tupled, Director.unapply)
  }

  val directors = TableQuery[Directors]
}
