package by.bsu.model.repository

import by.bsu.model.Db

case class Film(id: Option[Long], name: String, ageLimit: Option[String], shortDescription: Option[String], timing: Option[String], image: Option[String], releaseDate: String, awards: Option[String], languageId: Option[Int], isPublic: Boolean)

case class NewFilm(name: String, ageLimit: Option[String], actors: Option[Seq[String]], genres: Option[Seq[String]], countries: Option[Seq[String]], directors: Option[Seq[String]], shortDescription: Option[String], timing: Option[String], image: Option[String], releaseDate: String, awards: Option[String], language_name: Option[String])



trait FilmsTable extends LanguagesTable {
  this: Db =>

  import config.driver.api._

  class Films(tag: Tag) extends Table[Film](tag, "films") {
    def filmId = column[Long]("film_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def ageLimit = column[Option[String]]("age_limit")

    def shortDescription = column[Option[String]]("short_description")

    def timing = column[Option[String]]("timing")

    def image = column[Option[String]]("image")

    def releaseDate = column[String]("release_date")

    def awards = column[Option[String]]("awards")

    def languageId = column[Option[Int]]("language_id")

    def public = column[Boolean]("is_public")

    def fk_language_id = foreignKey("fk_language_id", languageId, languages)(_.language_id)

    def * = (filmId.?, name, ageLimit, shortDescription, timing, image, releaseDate, awards, languageId, public) <> (Film.tupled, Film.unapply)
  }

  val films = TableQuery[Films]
}
