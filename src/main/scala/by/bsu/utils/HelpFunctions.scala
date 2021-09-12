package by.bsu.utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait HelpFunctions {
  def foldEitherOfFuture[A, B](e: Either[A, Future[B]]): Future[Either[A, B]] =
    e match {
      case Left(s) => Future.successful(Left(s))
      case Right(f) => f.map(Right(_))
    }

  def isEnglish(str: String): Boolean ={
    ((!str.equals(""))
      && (str != null)
      && (str.matches("^[a-zA-Z0-9]*$")))
  }
}
