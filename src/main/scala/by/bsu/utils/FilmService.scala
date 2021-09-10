package by.bsu.utils

import by.bsu.model.Db
import by.bsu.model.dao._
import by.bsu.model.repository._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

trait FilmService extends Db with HelpFunctions with LinkedTablesService {

  val filmsDAO = new FilmsDAO(config)
  val actorsDAO = new ActorsDAO(config)
  val directorsDAO = new DirectorsDAO(config)
  val countriesDAO = new CountriesDAO(config)
  val genresDAO = new GenresDAO(config)
  val languagesDAO = new LanguagesDAO(config)


  def createFilm(newFilm: NewFilm) = {

    val actorsInsertion: Try[Future[Seq[Actor]]] = Try(Future.sequence(newFilm.actors.get.map(actor => actorsDAO.insertUniq(Actor(None, actor))))).map(_.map(_.map(_.toOption.get)))
    val genresInsertion: Try[Future[Seq[Genre]]] = Try(Future.sequence(newFilm.genres.get.map(genre => genresDAO.insertUniq(Genre(None, genre))))).map(_.map(_.map(_.toOption.get)))
    val directorsInsertion: Try[Future[Seq[Director]]] = Try(Future.sequence(newFilm.directors.get.map(director => directorsDAO.insertUniq(Director(None, director))))).map(_.map(_.map(_.toOption.get)))
    val countriesInsertion: Try[Future[Seq[Country]]] = Try(Future.sequence(newFilm.countries.get.map(country => countriesDAO.insertUniq(Country(None, country))))).map(_.map(_.map(_.toOption.get)))

    val filmsInsertion = filmsDAO.insertUniqFilmAndLanguages(newFilm)

    val filmsEntitiesTables = createLinkedTables(filmsInsertion, actorsInsertion, genresInsertion, directorsInsertion, countriesInsertion)

    (filmsEntitiesTables, actorsInsertion, genresInsertion, countriesInsertion, directorsInsertion, filmsInsertion)
  }

  def getAllFilms: Future[Seq[Film]] ={
    filmsDAO.findAll()
  }

  def getFilmById(id: Long) ={
    filmsDAO.findById(id)
  }

  def deleteById(id: Long)={
    filmsDAO.deleteById(id)
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

