package footballEtl

object DataValidator {

  val validPositions = List("Goalkeeper",
  "Defender",
  "Midfielder",
  "Forward")

  def isValid(p: Player): Boolean = {
    (p.age >= 16 && p.age <= 45) &&
      p.goalsScored >= 0 &&
      p.matchesPlayed > 0 &&
    validPositions.contains(p.position)
  }

  def filterValid(players: List[Player]): List[Player] =
    players.filter(isValid)

  def deduplicateById(players: List[Player]): List[Player] =
    players.foldLeft(List[Player]()) { (acc, p) =>
      if (acc.exists(_.id == p.id)) acc else acc :+ p
    }

  def validateAndDeduplicate(players: List[Player]): List[Player] =
    deduplicateById(filterValid(players))

}