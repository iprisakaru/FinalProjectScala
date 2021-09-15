package by.bsu.utils

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object HelpFunctions {

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext

  def fOption[A](x: Option[Future[A]])(implicit ec: ExecutionContext): Future[Option[A]] =
    x match {
      case Some(f) => f.map(Some(_))
      case None => Future.successful(None)
    }

  def isEnglish(str: String): Boolean = {
    ((!str.equals(""))
      && (str != null)
      && (str.matches("^[a-zA-Z0-9]*$")))
  }


}
