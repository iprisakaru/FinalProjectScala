package by.bsu.model.repository

import by.bsu.model.Db

case class Comment(commentId: Option[Int], header: String, description: String, rating: Byte,
                   recommended: Boolean, userId: Int, filmId: Int, recommendedFilm1: Option[Int],
                   recommendedFilm2: Option[Int], recommendedFilm3: Option[Int],
                   recommendedFilm4: Option[Int], recommendedFilm5: Option[Int])

trait CommentsTable extends FilmsTable with UsersTable {
  this: Db =>

  import config.driver.api._

  class Comments(tag: Tag) extends Table[Comment](tag, "comments") {
    def commentId = column[Int]("comment_id", O.PrimaryKey, O.AutoInc)

    def header = column[String]("header")

    def description = column[String]("description")

    def rating = column[Byte]("rating")

    def recommended = column[Boolean]("recommended")

    def userId = column[Int]("user_id")

    def filmId = column[Int]("film_id")

    def recommendedFilm1 = column[Int]("recommended_film_1")

    def recommendedFilm2 = column[Int]("recommended_film_2")

    def recommendedFilm3 = column[Int]("recommended_film_3")

    def recommendedFilm4 = column[Int]("recommended_film_4")

    def recommendedFilm5 = column[Int]("recommended_film_5")

    def fkFilmId = foreignKey("fk_film_id", filmId, films)(_.filmId)

    def fkRecommendedFilm1 = foreignKey("fk_recommended_film_1", recommendedFilm1, films)(_.filmId)

    def fkRecommendedFilm2 = foreignKey("fk_recommended_film_2", recommendedFilm2, films)(_.filmId)

    def fkRecommendedFilm3 = foreignKey("fk_recommended_film_3", recommendedFilm3, films)(_.filmId)

    def fkRecommendedFilm4 = foreignKey("fk_recommended_film_4", recommendedFilm4, films)(_.filmId)

    def fkRecommendedFilm5 = foreignKey("fk_recommended_film_5", recommendedFilm5, films)(_.filmId)

    def fkUserId = foreignKey("fk_user_id", userId, users)(_.user_id)

    def * = (commentId.?, header, description, rating, recommended, userId, filmId, recommendedFilm1.?,
      recommendedFilm2.?, recommendedFilm3.?, recommendedFilm4.?, recommendedFilm5.?) <> (Comment.tupled, Comment.unapply)
  }

  val comments = TableQuery[Comments]
}
