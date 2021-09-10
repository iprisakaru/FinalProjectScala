package by.bsu.utils

import by.bsu.model.Db
import by.bsu.model.dao._
import by.bsu.model.repository._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

trait FilmService extends Db with HelpFunctions {

  val filmsDAO = new FilmsDAO(config)
  val actorsDAO = new ActorsDAO(config)
  val directorsDAO = new DirectorsDAO(config)
  val countriesDAO = new CountriesDAO(config)
  val genresDAO = new GenresDAO(config)
  val languagesDAO = new LanguagesDAO(config)
  val actorsFilmsDAO = new ActorsFilmsDAO(config)
  val countriesFilmsDAO = new CountriesFilmsDAO(config)
  val directorsFilmsDAO = new DirectorsFilmsDAO(config)
  val genresFilmsDAO = new GenresFilmsDAO(config)

  def createFilm(newFilm: NewFilm) = {

    val actorsInsertion = Try(Future.sequence(newFilm.actors.map(actor => actorsDAO.insertUniq(Actor(None, actor))))).map(_.map(_.map(_.toOption.get)))
    val genresInsertion = Try(Future.sequence(newFilm.genres.map(genre => genresDAO.insertUniq(Genre(None, genre))))).map(_.map(_.map(_.toOption.get)))
    val directorsInsertion = Try(Future.sequence(newFilm.directors.map(director => directorsDAO.insertUniq(Director(None, director))))).map(_.map(_.map(_.toOption.get)))
    val countriesInsertion = Try(Future.sequence(newFilm.countries.map(country => countriesDAO.insertUniq(Country(None, country))))).map(_.map(_.map(_.toOption.get)))
    val filmsInsertion = filmsDAO.insertUniqFilmAndLanguages(newFilm)

    val filmsEntitiesTables = filmsInsertion.map(filmData => {
      if (filmData.isRight) {

        val filmId = filmData.toOption.get.id.get

        val filmsActorsTable = if (actorsInsertion.isSuccess) {
          Right(actorsInsertion.toOption.get.map(data => Future.sequence(data.map(actor => actorsFilmsDAO.insert(ActorFilm(None, actor.id.get, filmId))))).flatten)
        }
        else Left(new Exception + "Exception of inserting to actors_films database")

        val filmsGenresTable = if (genresInsertion.isSuccess) {
          Right(genresInsertion.toOption.get.map(data => Future.sequence(data.map(genre => (genresFilmsDAO.insert(GenreFilm(None, genre.id.get, filmId)))))).flatten)
        }
        else Left(new Exception + "Exception of inserting to genres_films database")

        val filmsDirectorsTable = if (directorsInsertion.isSuccess) {
          Right(directorsInsertion.toOption.get.map(data => Future.sequence(data.map(director => directorsFilmsDAO.insert(DirectorFilm(None, director.id.get, filmId))))).flatten)
        }
        else Left(new Exception + "Exception of inserting to directors_films database")

        val filmsCountriesTable = if (countriesInsertion.isSuccess) {
          Right(countriesInsertion.toOption.get.map(data => Future.sequence(data.map(country => countriesFilmsDAO.insert(CountryFilm(None, country.id.get, filmId))))).flatten)
        }
        else Left(new Exception + "Exception of inserting to countries_films database")

        Right(filmsActorsTable, filmsGenresTable, filmsCountriesTable, filmsDirectorsTable)
      }
      else Left(new Exception + "Impossible to get film Id")
    }).map(_.map(data => Future.sequence(List(foldEitherOfFuture(data._1), foldEitherOfFuture(data._2), foldEitherOfFuture(data._3), foldEitherOfFuture(data._4))))).map(data => foldEitherOfFuture(data)).flatten
    (filmsEntitiesTables, actorsInsertion, genresInsertion, countriesInsertion, directorsInsertion, filmsInsertion)
  }


  def deleteAll() = {
    Future.sequence(List(genresFilmsDAO.deleteAll(),
      directorsFilmsDAO.deleteAll(),
      countriesFilmsDAO.deleteAll(),
      actorsFilmsDAO.deleteAll(),
      actorsDAO.deleteAll(),
      genresDAO.deleteAll(),
      directorsDAO.deleteAll(),
      countriesDAO.deleteAll(),
      filmsDAO.deleteAll(),
      languagesDAO.deleteAll()))
  }

}

