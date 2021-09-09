package by.bsu.model.repository

import by.bsu.model.Db

case class Genre(id: Option[Int], name: String)

trait GenresTable {
  this: Db =>

  import config.driver.api._

  class Genres(tag: Tag) extends Table[Genre](tag, "genres") {
    def genre_id = column[Int]("genre_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (genre_id.?, name) <> (Genre.tupled, Genre.unapply)
  }

  val genres = TableQuery[Genres]

}
