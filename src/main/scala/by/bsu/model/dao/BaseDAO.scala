package by.bsu.model.dao

import by.bsu.model.Db

import scala.concurrent.Future

abstract class BaseDAO
  extends Db {

  type T

  def findAll(): Future[Seq[T]]

  def insert(entity: T): Future[Option[T]]

  def update(id: Int, actor: T): Future[Int]

  def deleteById(id: Int): Future[Boolean]

  def findById(id: Int): Future[Option[T]]

  def findByName(name: String): Future[Option[T]]

  def deleteAll(): Future[Int]

  def insertList(entities: Seq[T]): Future[Seq[Option[T]]]

}


