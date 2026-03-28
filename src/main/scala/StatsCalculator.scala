package footballEtl

object StatsCalculator {

  def ParsingStats(parsedPlayers: List[Player], parsingErrors: Int): PlayerStats = {
    val totalParsed = parsedPlayers.length
    val validPlayers = DataValidator.filterValid(parsedPlayers)
    val totalValid = validPlayers.length

    val deduplicatedPlayers = DataValidator.deduplicateById(validPlayers)
    val duplicatesRemoved = totalValid - deduplicatedPlayers.length

    PlayerStats(
      total_players_parsed = totalParsed,
      total_players_valid = deduplicatedPlayers.length,
      parsing_errors = parsingErrors,
      duplicates_removed = duplicatesRemoved
    )
  }

  def top10Scorers(players: List[Player]): List[TopScorer] =
    players
      .sortBy(p => -p.goalsScored)
      .take(10)
      .map(p =>
        TopScorer(
          name = p.name,
          club = p.club,
          goals = p.goalsScored,
          matches = p.matchesPlayed
        )
      )

  def top10Assisters(players: List[Player]): List[TopAssister] =
    players
      .sortBy(p => -p.assists)
      .take(10)
      .map(p =>
        TopAssister(
          p.name,
          p.club,
          p.assists,
          p.matchesPlayed
        )
      )

  def top10MarketValues(players: List[Player]): List[TopMarketValue] =
    players
      .flatMap(p =>
        p.marketValue.map(v =>
          TopMarketValue(p.name, p.club, v)
        )
      )
      .sortBy(p => -p.marketValue)
      .take(10)

  def top10Salaries(players: List[Player]): List[TopSalary] =
    players
      .flatMap(p =>
        p.salary.map(s =>
          TopSalary(p.name, p.club, s)
        )
      )
      .sortBy(p => -p.salary)
      .take(10)


  def countByLeague(players: List[Player]): Map[String, Int] =
    players.groupBy(_.league).view.mapValues(_.size).toMap

  def countByPosition(players: List[Player]): Map[String, Int] =
    players.groupBy(_.position).view.mapValues(_.size).toMap

  def averageAgeByPosition(players: List[Player]): Map[String, Double] =
    players
      .groupBy(_.position)
      .view
      .mapValues(ps => ps.map(_.age).sum.toDouble / ps.size)
      .toMap

  def averageGoalsPerMatchByPosition(players: List[Player]): Map[String, Double] =
    players
      .groupBy(_.position)
      .view
      .mapValues { ps =>
        ps.map(p => p.goalsScored.toDouble / p.matchesPlayed).sum / ps.size
      }
      .toMap

  def totalYellowCards(players: List[Player]): Int =
    players.map(_.yellowCards).sum

  def totalRedCards(players: List[Player]): Int =
    players.map(_.redCards).sum

  def cardsByPosition(players: List[Player]): Map[String, Int] =
    players
      .groupBy(_.position)
      .view
      .mapValues(ps => ps.map(p => p.yellowCards + p.redCards).sum)
      .toMap

  def mostDisciplinedPosition(players: List[Player]): String = {
    val byPosition = cardsByPosition(players)
    byPosition.minBy(_._2)._1
  }

  def leastDisciplinedPosition(players: List[Player]): String = {
    val byPosition = cardsByPosition(players)
    byPosition.maxBy(_._2)._1
  }

  def disciplineStats(players: List[Player]): DisciplineStats =
    DisciplineStats(
      totalYellowCards = totalYellowCards(players),
      totalRedCards = totalRedCards(players),
      mostDisciplinedPosition = mostDisciplinedPosition(players),
      leastDisciplinedPosition = leastDisciplinedPosition(players)
    )

  // Partie concernant le bonus
  def goalsPerMatch(p: Player): Double =
    p.goalsScored.toDouble / p.matchesPlayed.toDouble

  def top10Efficiency(players: List[Player], minMatches: Int = 10): List[TopEfficiency] =
    players
      .filter(p => p.matchesPlayed >= minMatches)
      .map { p =>
        TopEfficiency(
          name = p.name,
          club = p.club,
          goals = p.goalsScored,
          matches = p.matchesPlayed,
          goalsPerMatch = goalsPerMatch(p)
        )
      }
      .sortBy(t => -t.goalsPerMatch)
      .take(10)


  def top10ValueForMoney(players: List[Player]): List[TopValueForMoney] =
    players
      .flatMap { p =>
        p.salary.map { s =>
          TopValueForMoney(
            name = p.name,
            club = p.club,
            goals = p.goalsScored,
            salary = s,
            goalsPerSalary = p.goalsScored.toDouble / s
          )
        }
      }
      .sortBy(t => -t.goalsPerSalary)
      .take(10)


  def leagueStats(players: List[Player]): List[LeagueBonusStats] =
    players
      .groupBy(_.league)
      .map { case (league, ps) =>
        val avgAge = ps.map(_.age).sum.toDouble / ps.length
        val avgGoals = ps.map(_.goalsScored).sum.toDouble / ps.length
        LeagueBonusStats(league, avgAge, avgGoals)
      }
      .toList
      .sortBy(_.league)

  def mostProductiveLeague(players: List[Player]): String = {
    val stats = leagueStats(players)
    if (stats.isEmpty) "N/A"
    else stats.maxBy(_.avgGoals).league
  }

  def bonusReport(players: List[Player]): BonusReport =
    BonusReport(
      topEfficientPlayers = top10Efficiency(players, minMatches = 10),
      topValueForMoney = top10ValueForMoney(players),
      leagueStats = leagueStats(players),
      mostProductiveLeague = mostProductiveLeague(players)
    )

}
