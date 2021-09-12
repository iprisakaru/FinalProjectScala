package by.bsu.utils

import by.bsu.model.Db
import by.bsu.model.dao.{ActorsFilmsDAO, CountriesFilmsDAO, DirectorsFilmsDAO, GenresFilmsDAO}
import by.bsu.model.repository.{Actor, ActorFilm, Country, CountryFilm, Director, DirectorFilm, Film, Genre, GenreFilm}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

trait LinkedTablesDbService extends Db with HelpFunctions {

  val actorsFilmsDAO = new ActorsFilmsDAO(config)
  val countriesFilmsDAO = new CountriesFilmsDAO(config)
  val directorsFilmsDAO = new DirectorsFilmsDAO(config)
  val genresFilmsDAO = new GenresFilmsDAO(config)

  def createLinkedTables(filmsInsertion: Future[Either[Throwable, Film]], actorsInsertion: Try[Future[Seq[Actor]]],
                         genresInsertion: Try[Future[Seq[Genre]]], directorsInsertion: Try[Future[Seq[Director]]],
                         countriesInsertion: Try[Future[Seq[Country]]]) = {

    filmsInsertion.map(filmData => {
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


  }

}
