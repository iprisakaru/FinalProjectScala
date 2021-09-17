package by.bsu.utils

import by.bsu.model.dao.CountriesDAO
import by.bsu.model.repository.Country

class CountriesService(countriesDao: CountriesDAO) {

  def getById(id: Int) = {
    countriesDao.findById(id)
  }

  def getAll() = {
    countriesDao.findAll()
  }

  def create(country: Country) = {
    countriesDao.insertUniq(country)
  }

  def createList(countryList: Seq[Country]) = {
    countriesDao.insertListCountry(countryList)
  }

  def update(id: Int, country: Country) = {
    countriesDao.update(id, country)
  }

  def deleteById(id: Int) = {
    countriesDao.deleteById(id)
  }

  def deleteAll() = {
    countriesDao.deleteAll()
  }

}
