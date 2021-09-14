package by.bsu.utils

import by.bsu.model.dao.LanguagesDAO
import by.bsu.model.repository.Language

import scala.concurrent.Future

class LanguagesService(languagesDao: LanguagesDAO) {
  def getById(id: Int) = {
    languagesDao.findById(id)
  }

  def getAll() = {
    languagesDao.findAll()
  }

  def create(genre: Language) = {
    languagesDao.insertUniq(genre)
  }

  def createList(languagesList: Seq[Language]) = {
    languagesDao.insertListLanguages(languagesList)
  }

  def update(id: Int, genre: Language) = {
    languagesDao.update(id, genre)
  }

  def deleteById(id: Int) = {
    languagesDao.deleteById(id)
  }

  def deleteAll() = {
    languagesDao.deleteAll()
  }

}
