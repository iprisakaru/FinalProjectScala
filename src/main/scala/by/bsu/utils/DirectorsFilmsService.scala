package by.bsu.utils

import by.bsu.model.dao.DirectorsFilmsDAO
import by.bsu.model.repository.DirectorFilm

class DirectorsFilmsService(directorsFilmsDAO: DirectorsFilmsDAO) {
  def getByName(actorId: Int, filmId: Int) = {
    directorsFilmsDAO.findByName(actorId, filmId)
  }

  def getAll() = {
    directorsFilmsDAO.findAll()
  }

  def createList(directorsFilmList: Seq[DirectorFilm]) = {
    directorsFilmsDAO.insertListDirectorFilm(directorsFilmList)
  }

  def deleteById(directorsId: Int, filmId: Int) = {
    directorsFilmsDAO.deleteById(directorsId, filmId)
  }

  def deleteAll() = {
    directorsFilmsDAO.deleteAll()
  }
}