package footballEtl

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import scala.io.Source
import scala.util.{Try, Success, Failure}

object DataLoader {

  def loadPlayer(filename: String): Either[String, (List[Player], Int)] = {
    Try {
      val source = Source.fromFile(filename)
      val content = source.mkString
      source.close()
      content
    } match {
      case Success(content) =>
        decode[List[Json]](content) match {
          case Right(jsons) =>
            val (players, errors) = jsons.foldLeft((List[Player](), 0)) {
              case ((accPlayers, accErrors), json) =>
                decode[Player](json.noSpaces) match {
                  case Right(player) => 
                    (accPlayers :+ player, accErrors)
                  case Left(_) => 
                    (accPlayers, accErrors + 1)
                }
            }
            Right((players, errors))
            
          case Left(error) =>
            Left(s"Parsing error: ${error.getMessage}")
        }
        
      case Failure(exception) =>
        Left(s"File error: ${exception.getMessage}")
    }
  }
}