package by.bsu.model

case class Film(id: Option[Long], name: String, ageLimit: String, description: String, timing: String, image: String, releaseData: String, awards: String, language: Int)

case class Actor(id: Option[Int], name: String)

case class Country(id: Option[Int], name: String)

case class Director(id: Option[Int], name: String)

case class Genre(id: Option[Int], name: String)

case class Language(id: Option[Int], name: String)

case class Admin(id: Option[Int], code: String)

case class User(id: Option[Int], code: String)

case class ActorInFilm(actorId: Int, filmId: Long)

case class CountryInFilm(countryId: Int, filmId: Long)

case class DirectorInFilm(directorId: Int, filmId: Long)

case class GenreInFilm(genreId: Int, filmId: Long)