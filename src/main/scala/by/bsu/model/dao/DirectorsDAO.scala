package by.bsu.model.dao

import by.bsu.model.repository.{Director, DirectorsTable}
import by.bsu.model.Db
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class DirectorsDAO(val config: DatabaseConfig[JdbcProfile])
  extends Db with DirectorsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def insert(director: Director): Future[Director] = {
    db.run(directors returning directors.map(_.director_id) += director)
      .map(id => director.copy(id = Option(id)))
  }


  def update(id: Int, director: Director): Future[Int] = {
    db.run(directors.filter(_.director_id === id).map(customer => (customer.name))
      .update(director.name))
  }

  def findAll(): Future[Seq[Director]] = db.run(directors.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(directors.filter(_.director_id === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[Director]] = {
    db.run(directors.filter(_.director_id === id).result.headOption)
  }

  def findByName(name: String): Future[Option[Director]] = {
    db.run(directors.filter(_.name === name).result.headOption)
  }

  def insertUniq(director: Director): Future[Either[String, Future[Director]]] = {
    db.run(directors.filter(_.name === director.name).result).map(_.nonEmpty).map(isNotUniq => {
      if (isNotUniq) Left(new Exception + s" ${director.name} is already exist in database.")
      else Right(insert(director))
    })

  }

  def deleteAll(): Future[Int] = {
    db.run(directors.delete)
  }
}