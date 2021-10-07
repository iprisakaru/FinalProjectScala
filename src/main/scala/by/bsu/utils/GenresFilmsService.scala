package by.bsu.utils

import by.bsu.model.dao.GenresFilmsDAO
import by.bsu.model.repository.GenreFilm

import scala.language.postfixOps

class GenresFilmsService(genresFilmsDAO: GenresFilmsDAO) {
  def getByName(filmId: Int) = {
    genresFilmsDAO.findByFilm(filmId)
  }

  def getAll() = {
    genresFilmsDAO.findAll()
  }

  def createList(genreFilmList: Seq[GenreFilm]) = {
    genresFilmsDAO.insertListGenresFilm(genreFilmList)
  }

  def deleteById(genreId: Int, filmId: Int) = {
    genresFilmsDAO.deleteById(genreId, filmId)
  }

  def deleteAll() = {
    genresFilmsDAO.deleteAll()
  }

}