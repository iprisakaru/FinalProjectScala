package by.bsu.utils

import by.bsu.model.dao.CountriesFilmsDAO
import by.bsu.model.repository.CountryFilm

class CountriesFilmsService(countriesFilmsDAO: CountriesFilmsDAO) {
  def getByName(countryId: Int, filmId: Long) = {
    countriesFilmsDAO.findByName(countryId, filmId)
  }

  def getAll() = {
    countriesFilmsDAO.findAll()
  }

  def create(countryFilm: CountryFilm) = {
    countriesFilmsDAO.insertCountryFilm(countryFilm)
  }

  def createList(countryFilmList: Seq[CountryFilm]) = {
    countriesFilmsDAO.insertListCountryFilm(countryFilmList)
  }

  def deleteById(countryId: Int, filmId: Long) = {
    countriesFilmsDAO.deleteById(countryId, filmId)
  }

  def deleteAll() = {
    countriesFilmsDAO.deleteAll()
  }

}