package by.bsu.utils

import by.bsu.model.dao.ActorsFilmsDAO
import by.bsu.model.repository.ActorFilm

import scala.concurrent.Future

class ActorsFilmsService(actorsFilmsDao: ActorsFilmsDAO) {
  def getByName(actorId: Int, filmId: Int) = {
    actorsFilmsDao.findByName(actorId, filmId)
  }

  def getAll(): Future[Seq[ActorFilm]] = {
    actorsFilmsDao.findAll()
  }

  def create(actorFilm: ActorFilm) = {
    actorsFilmsDao.insertActorFilm(actorFilm)
  }

  def createList(actorFilmList: Seq[ActorFilm]) = {
    actorsFilmsDao.insertListActorFilm(actorFilmList)
  }

  def deleteById(actorId: Int, filmId: Int) = {
    actorsFilmsDao.deleteById(actorId, filmId)
  }

  def deleteAll() = {
    actorsFilmsDao.deleteAll()
  }

}
