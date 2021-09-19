package by.bsu.utils

import by.bsu.model.dao.CommentsDAO
import by.bsu.model.repository.Comment

import scala.concurrent.Future

class CommentsService(commentsDao: CommentsDAO) {

  def create(comment: Comment): Future[Option[Comment]] = {
    commentsDao.insert(comment)
  }

  def deleteById(id: Int): Future[Boolean] = {
    commentsDao.deleteById(id)
  }

  def updateById(id: Int, comment: Comment): Future[Int] = {
    commentsDao.update(id, comment)
  }

  def getByFilmId(filmId: Int): Future[Seq[Comment]] = {
    commentsDao.findByFilmId(filmId)
  }
}
