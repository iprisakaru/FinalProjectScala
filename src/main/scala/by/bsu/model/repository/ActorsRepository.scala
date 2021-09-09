package by.bsu.model.repository

import by.bsu.model.Db

case class Actor(id: Option[Int], name: String)

trait ActorsTable{
  this: Db =>

  import config.driver.api._

  class Actors(tag: Tag) extends Table[Actor](tag, "actors") {
    def actor_id = column[Int]("actor_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (actor_id.?, name) <> (Actor.tupled, Actor.unapply)
  }

  val actors = TableQuery[Actors]

}
