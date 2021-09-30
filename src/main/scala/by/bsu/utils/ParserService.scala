package by.bsu.utils

import akka.http.scaladsl.server.RequestContext
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import by.bsu.model.repository.NewFilmWithFields

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class ParserService {

  def parseCSVtoFilm(source: Source[ByteString, Any], ctx: RequestContext) = {
    implicit val materializer = ctx.materializer

    val sink = Sink.fold[String, ByteString]("") { case (acc, str) =>
      acc + str.decodeString("US-ASCII")
    }
    val list = source.runWith(sink).map(_.split("\n").toSeq)
    val naming = list.map(_.head.split(",").map(_.replaceAll("\r", "")).toSeq)
    val data = list.map(_.tail.map(_.split("\"").filter(info => info != "," && info != "\r").map(_.split(",").toSeq).toSeq))
    val result = for {
      namingMapFut <- naming.map(_.filter(info => info != "genres" && info != "countries" && info != "actors" && info != "directors").zipWithIndex.toMap)
      nameEntitiesFut <- naming.map(_.filter(info => info == "genres" || info == "countries" || info == "actors" || info == "directors").zipWithIndex.toMap)

      nameFut <- data.map(_.map(info => NewFilmWithFields(None, info.head(namingMapFut("name")),
        Try(info.head(namingMapFut("ageLimit"))).toOption, Try(info(nameEntitiesFut("actors") + 1)).toOption,
        Try(info(nameEntitiesFut("genres") + 1)).toOption, Try(info(nameEntitiesFut("countries") + 1)).toOption, Try(info(nameEntitiesFut("directors") + 1)).toOption,
        Try(info.head(namingMapFut("shortDecription"))).toOption, Try(info.head(namingMapFut("timing"))).toOption, Try(info.head(namingMapFut("image"))).toOption,
        info.head(namingMapFut("releaseDate")), Try(info.head(namingMapFut("awards"))).toOption, Try(info.head(namingMapFut("languages"))).toOption, Option(false))))


    } yield (nameFut)
    result.flatMap(info => Future.sequence(info.map(RouteService.filmsService.insertFilmWithFields))).map(_.map(Option(_)))

  }
  
}
