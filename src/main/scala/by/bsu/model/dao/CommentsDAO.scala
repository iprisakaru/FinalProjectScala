package by.bsu.model.dao

import by.bsu.model.repository.{Comment, CommentsTable}
import org.apache.log4j.Logger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class CommentsDAO(val config: DatabaseConfig[JdbcProfile])
  extends BaseDAO with CommentsTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  override type T = Comment

  val LOGGER = Logger.getLogger(this.getClass.getName)

  def insert(comment: Comment): Future[Option[Comment]] = {
    LOGGER.debug(s"Inserting comment of user ${comment.userId} id")
    db.run(((comments returning comments) += comment).asTry).map(_.toOption)
  }


  def update(id: Int, comment: Comment): Future[Int] = {
    LOGGER.debug(s"Updating comment to comment $id id")
    db.run(comments.filter(_.commentId === id).map(customer => (customer.header, customer.description,
      customer.rating, customer.recommendedFilm1.?, customer.recommendedFilm2.?, customer.recommendedFilm3.?,
      customer.recommendedFilm4.?, customer.recommendedFilm5.?))
      .update(comment.header, comment.description, comment.rating, comment.recommendedFilm1, comment.recommendedFilm2,
        comment.recommendedFilm3, comment.recommendedFilm4, comment.recommendedFilm5))
  }

  def findAll(): Future[Seq[Comment]] = db.run(comments.result)

  def deleteById(id: Int): Future[Boolean] = {
    db.run(comments.filter(_.commentId === id).delete) map {
      _ > 0
    }
  }

  def findById(id: Int): Future[Option[Comment]] = {
    db.run(comments.filter(_.commentId === id).result.headOption)
  }

  def findByFilmId(id: Int) = {
    db.run(comments.filter(_.filmId === id).result)
  }

  def findByName(name: String): Future[Option[Comment]] = ???


  def deleteAll(): Future[Int] = {
    db.run(comments.delete)
  }

  override def insertList(entities: Seq[Comment]): Future[Seq[Option[Comment]]] = ???
}
