package by.bsu.model

trait AddressTable {
  this: Db =>

  import config.driver.api._

  class Admins(tag: Tag) extends Table[Admin](tag, "admins") {
    def actor_id = column[Int]("actor_id", O.PrimaryKey, O.AutoInc)

    def code = column[String]("code")

    def * = (actor_id.?, code) <> (Admin.tupled, Admin.unapply)
  }

  val admins = TableQuery[Admins]

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def user_id = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

    def code = column[String]("code")

    def * = (user_id.?, code) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]

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

    def fk_language_id = foreignKey("fk_language_id", language_id, languages)(_.language_id)

    def * = (film_id.?, name, age_limit, short_description, timing, image, release_date, awards, language_id) <> (Film.tupled, Film.unapply)
  }

  val films = TableQuery[Films]

  class Languages(tag: Tag) extends Table[Language](tag, "language") {
    def language_id = column[Int]("language_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (language_id.?, name) <> (Language.tupled, Language.unapply)
  }

  val languages = TableQuery[Languages]

  class Countries(tag: Tag) extends Table[Country](tag, "countries") {
    def country_id = column[Int]("country_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (country_id.?, name) <> (Country.tupled, Country.unapply)
  }

  val countries = TableQuery[Countries]

  class Genres(tag: Tag) extends Table[Genre](tag, "genres") {
    def genre_id = column[Int]("genre_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (genre_id.?, name) <> (Genre.tupled, Genre.unapply)
  }

  val genres = TableQuery[Genres]

  class Directors(tag: Tag) extends Table[Director](tag, "directors") {
    def director_id = column[Int]("director_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (director_id.?, name) <> (Director.tupled, Director.unapply)
  }

  val directors = TableQuery[Directors]

  class Actors(tag: Tag) extends Table[Actor](tag, "actors") {
    def actor_id = column[Int]("actor_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (actor_id.?, name) <> (Actor.tupled, Actor.unapply)
  }

  val actors = TableQuery[Actors]

  class ActorsInFilms(tag: Tag) extends Table[ActorInFilm](tag, "actors_in_films") {
    def actor_id = column[Int]("actor_id", O.PrimaryKey)

    def film_id = column[Long]("film_id", O.PrimaryKey)

    def fk_actor_id = foreignKey("fk_actor_id", actor_id, actors)(_.actor_id)

    def fk_film_id = foreignKey("fk_film_id", film_id, films)(_.film_id)

    def * = (actor_id, film_id) <> (ActorInFilm.tupled, ActorInFilm.unapply)
  }

  val actorsInFilms = TableQuery[ActorsInFilms]

  class GenresInFilms(tag: Tag) extends Table[GenreInFilm](tag, "genres_in_films") {
    def genre_id = column[Int]("genre_id", O.PrimaryKey)

    def film_id = column[Long]("film_id", O.PrimaryKey)

    def fk_genre_id = foreignKey("fk_genre_id", genre_id, genres)(_.genre_id)

    def fk_film_id = foreignKey("fk_film_id", film_id, films)(_.film_id)

    def * = (genre_id, film_id) <> (GenreInFilm.tupled, GenreInFilm.unapply)
  }

  val genresInFilms = TableQuery[GenresInFilms]

  class DirectorsInFilms(tag: Tag) extends Table[DirectorInFilm](tag, "directors_in_films") {
    def director_id = column[Int]("director_id", O.PrimaryKey)

    def film_id = column[Long]("film_id", O.PrimaryKey)

    def fk_actor_id = foreignKey("fk_actor_id", director_id, directors)(_.director_id)

    def fk_film_id = foreignKey("fk_film_id", film_id, films)(_.film_id)

    def * = (director_id, film_id) <> (DirectorInFilm.tupled, DirectorInFilm.unapply)
  }

  val directorsInFilms = TableQuery[DirectorsInFilms]

  class CountriesInFilms(tag: Tag) extends Table[CountryInFilm](tag, "countries_in_films") {
    def country_id = column[Int]("country_id", O.PrimaryKey)

    def film_id = column[Long]("film_id", O.PrimaryKey)

    def fk_country_id = foreignKey("fk_country_id", country_id, countries)(_.country_id)

    def fk_film_id = foreignKey("fk_film_id", film_id, films)(_.film_id)

    def * = (country_id, film_id) <> (CountryInFilm.tupled, CountryInFilm.unapply)
  }

  val countriesInFilms = TableQuery[CountriesInFilms]
}
