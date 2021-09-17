package by.bsu.utils

import by.bsu.model.dao.DirectorsFilmsDAO
import by.bsu.model.repository.DirectorFilm

class DirectorsFilmsService(directorsFilmsDAO: DirectorsFilmsDAO) {
  def getByName(actorId: Int, filmId: Long) = {
    directorsFilmsDAO.findByName(actorId, filmId)
  }

  def getAll() = {
    directorsFilmsDAO.findAll()
  }

  def createList(directorsFilmList: Seq[DirectorFilm]) = {
    directorsFilmsDAO.insertListDirectorFilm(directorsFilmList)
  }

  def deleteById(directorsId: Int, filmId: Long) = {
    directorsFilmsDAO.deleteById(directorsId, filmId)
  }

  def deleteAll() = {
    directorsFilmsDAO.deleteAll()
  }
}