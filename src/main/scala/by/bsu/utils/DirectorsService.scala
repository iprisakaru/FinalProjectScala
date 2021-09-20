package by.bsu.utils

import by.bsu.model.dao.DirectorsDAO
import by.bsu.model.repository.Director

import scala.concurrent.Future

class DirectorsService(directorsDao: DirectorsDAO) {

  def getById(id: Int) = {
    directorsDao.findById(id)
  }

  def getAll() = {
    directorsDao.findAll()
  }

  def getByName(name: String): Future[Option[Director]] = {
    directorsDao.findByName(name)
  }

  def create(country: Director): Future[Option[Director]] = {
    directorsDao.insert(country)
  }

  def createList(directorsFilmList: Seq[Director]): Future[Seq[Option[Director]]] = {
    directorsDao.insertList(directorsFilmList)
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
