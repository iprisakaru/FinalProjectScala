package by.bsu.model.repository

import by.bsu.model.Db

case class Film(id: Option[Long], name: String, age_limit: Option[String], short_description: String, timing: Option[String], image: Option[String], release_date: String, awards: Option[String], language_id: Option[Int], isPublic: Boolean)

case class NewFilm( name: String, age_limit: String, actors: Seq[String], genres: Seq[String], countries: Seq[String], directors: Seq[String], short_description: String, timing: String, image: String, release_date: String, awards: String, language_name: String)

case class NewFilmWithoutId(id: Option[Long], name: String, age_limit: String, short_description: String, timing: String, image: String, release_date: String, awards: String, language_id: String)


trait FilmsTable extends LanguagesTable {
  this: Db =>

  import config.driver.api._

  class Films(tag: Tag) extends Table[Film](tag, "films") {
    def film_id = column[Long]("film_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def age_limit = column[String]("age_limit")

    def short_description = column[String]("short_description")

    def timing = column[String]("timing")

    def image = column[String]("image")

    def release_date = column[String]("release_date")

    def awards = column[String]("awards")

    def language_id = column[Int]("language_id")

    def isPublic = column[Boolean]("is_public")

    def fk_language_id = foreignKey("fk_language_id", language_id, languages)(_.language_id)

    def * = (film_id.?, name, age_limit.?, short_description, timing.?, image.?, release_date, awards.?, language_id.?, isPublic) <> (Film.tupled, Film.unapply)
  }

  val films = TableQuery[Films]
}
