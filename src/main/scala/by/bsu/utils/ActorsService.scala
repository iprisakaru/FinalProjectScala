package by.bsu.utils

import by.bsu.model.dao.ActorsDAO
import by.bsu.model.repository.Actor

class ActorsService(actorsDao: ActorsDAO) {

  def getById(id: Int) = {
    actorsDao.findById(id)
  }

  def getAll() = {
    actorsDao.findAll()
  }

  def create(country: Actor) = {
    actorsDao.insertUniq(country)
  }

  def createList(actorsList: Seq[Actor]) = {
    actorsDao.insertListActor(actorsList)
  }

  def update(id: Int, country: Actor) = {
    actorsDao.update(id, country)
  }

  def deleteById(id: Int) = {
    actorsDao.deleteById(id)
  }

  def deleteAll() = {
    actorsDao.deleteAll()
  }

}
