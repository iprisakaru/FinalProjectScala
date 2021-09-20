package by.bsu.utils

import by.bsu.Application.dbConfig
import by.bsu.model.dao._

object RouteService {

  //main tables
  val filmsService = new FilmsService(new FilmsDAO(dbConfig))
  val directorsService = new DirectorsService(new DirectorsDAO(dbConfig))
  val languagesService = new LanguagesService(new LanguagesDAO(dbConfig))
  val genresService = new GenresService(new GenresDAO(dbConfig))
  val countriesService = new CountriesService(new CountriesDAO(dbConfig))
  val actorsService = new ActorsService(new ActorsDAO(dbConfig))

  //additional tables
  val directorsFilmsService = new DirectorsFilmsService(new DirectorsFilmsDAO(dbConfig))
  val genresFilmsService = new GenresFilmsService(new GenresFilmsDAO(dbConfig))
  val countriesFilmsService = new CountriesFilmsService(new CountriesFilmsDAO(dbConfig))
  val actorsFilmsService = new ActorsFilmsService(new ActorsFilmsDAO(dbConfig))

  //users and admins tables

  val adminsService = new AdminsService(new AdminsDAO(dbConfig))
  val commentsService = new CommentsService(new CommentsDAO(dbConfig))
}
