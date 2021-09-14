package by.bsu.utils

import by.bsu.model.dao.GenresDAO
import by.bsu.model.repository.Genre
import by.bsu.web.api.UpdatingDataController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class GenresService(genresDao: GenresDAO) {
  val updateDataController = new UpdatingDataController

  def getById(id: Int) = {
    genresDao.findById(id)
  }

  def getAll() = {
    genresDao.findAll()
  }

  def create(genre: Genre) = {
    genresDao.insertUniq(genre)
  }

  def update(id: Int, genre: Genre) = {
    genresDao.update(id, genre)
  }

  def createList(genresList: Seq[Genre]) = {
    genresDao.insertListGenres(genresList)
  }

  def deleteById(id: Int) = {
    genresDao.deleteById(id)
  }

  def deleteAll() = {
    genresDao.deleteAll()
  }

  def getGenresFromApi = {
    updateDataController.getGenresFromApi().flatMap(fut => Future.sequence(fut.map(tmp => Future.sequence(tmp.genres.map(genre => create(Genre(None, genre.name)))))))

  }

}
