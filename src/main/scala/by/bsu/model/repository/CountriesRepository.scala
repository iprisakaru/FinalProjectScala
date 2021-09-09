package by.bsu.model.repository

import by.bsu.model.Db

case class Country(id: Option[Int], name: String)

trait CountriesTable{
  this: Db =>

  import config.driver.api._

  class Countries(tag: Tag) extends Table[Country](tag, "countries") {
    def country_id = column[Int]("country_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (country_id.?, name) <> (Country.tupled, Country.unapply)
  }

  val countries = TableQuery[Countries]
}
