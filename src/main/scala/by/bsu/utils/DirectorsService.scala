package by.bsu.utils

import by.bsu.model.dao.DirectorsDAO
import by.bsu.model.repository.Director

class DirectorsService(directorsDao: DirectorsDAO) {

  def getById(id: Int) = {
    directorsDao.findById(id)
  }

  def getAll() = {
    directorsDao.findAll()
  }

  def create(country: Director) = {
    directorsDao.insertUniq(country)
  }

  def createList(directorsFilmList: Seq[Director]) = {
    directorsDao.insertListDirectors(directorsFilmList)
  }

  def update(id: Int, director: Director) = {
    directorsDao.update(id, director)
  }

  def deleteById(id: Int) = {
    directorsDao.deleteById(id)
  }

  def deleteAll() = {
    directorsDao.deleteAll()
  }

}
