package by.bsu.model.repository

import by.bsu.model.Db

case class Film(id: Option[Int], name: String, ageLimit: Option[String], shortDescription: Option[String],
                timing: Option[String], image: Option[String], releaseDate: String, awards: Option[String],
                languageId: Option[Int], isPublic: Option[Boolean])

case class NewFilmWithFields(id: Option[Int], name: String, ageLimit: Option[String], actors: Option[Seq[String]], genres: Option[Seq[String]],
                             countries: Option[Seq[String]], directors: Option[Seq[String]], shortDescription: Option[String],
                             timing: Option[String], image: Option[String], releaseDate: String, awards: Option[String],
                             languageName: Option[String], isPublic: Option[Boolean])

case class NewFilmWithId(id: Option[Int], name: String, ageLimit: Option[String], actorsId: Option[Seq[Int]],
                         genresId: Option[Seq[Int]], countriesId: Option[Seq[Int]], directorsId: Option[Seq[Int]],
                         shortDescription: Option[String], timing: Option[String], image: Option[String], releaseDate: String,
                         awards: Option[String], languageId: Option[Int], isPublic: Option[Boolean])

case class NewFilmWithFieldsId(id: Option[Int], name: String, ageLimit: Option[String], actors: Option[Seq[(Int, String)]],
                               genres: Option[Seq[(Int, String)]], countries: Option[Seq[(Int, String)]], directors: Option[Seq[(Int, String)]],
                               shortDescription: Option[String], timing: Option[String], image: Option[String], releaseDate: String,
                               awards: Option[String], languages: Option[(Int, String)], isPublic: Option[Boolean])

trait FilmsTable extends LanguagesTable {
  this: Db =>

  import config.driver.api._


  class Films(tag: Tag) extends Table[Film](tag, "films") {
    def filmId = column[Int]("film_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def ageLimit = column[Option[String]]("age_limit")

    def shortDescription = column[Option[String]]("short_description")

    def timing = column[Option[String]]("timing")

    def image = column[Option[String]]("image")

    def releaseDate = column[String]("release_date")

    def awards = column[Option[String]]("awards")

    def languageId = column[Option[Int]]("language_id")

    def public = column[Option[Boolean]]("is_public")

    def fk_language_id = foreignKey("fk_language_id", languageId, languages)(_.language_id)

    def * = (filmId.?, name, ageLimit, shortDescription, timing, image, releaseDate, awards, languageId, public) <> (Film.tupled, Film.unapply)
  }

  val films = TableQuery[Films]
}
