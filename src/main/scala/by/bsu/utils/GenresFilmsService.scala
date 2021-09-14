package by.bsu.utils

import by.bsu.model.dao.GenresFilmsDAO
import by.bsu.model.repository.GenreFilm

class GenresFilmsService(genresFilmsDAO: GenresFilmsDAO) {
  def getByName(genreId: Int, filmId: Long) = {
    genresFilmsDAO.findByName(genreId, filmId)
  }

  def getAll() = {
    genresFilmsDAO.findAll()
  }

  def create(genreFilm: GenreFilm) = {
    genresFilmsDAO.insertGenresFilms(genreFilm)
  }

  def createList(genreFilmList: Seq[GenreFilm]) = {
    genresFilmsDAO.insertListGenresFilm(genreFilmList)
  }

  def deleteById(genreId: Int, filmId: Long) = {
    genresFilmsDAO.deleteById(genreId, filmId)
  }

  def deleteAll() = {
    genresFilmsDAO.deleteAll()
  }
}