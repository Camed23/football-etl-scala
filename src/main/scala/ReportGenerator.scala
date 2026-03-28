package footballEtl

import io.circe.generic.auto._
import io.circe.syntax._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.{Try, Success, Failure}

object ReportGenerator {

  def generateReport(playersParsed: List[Player], parsingErrors: Int): AnalysisReport = {

    val validPlayers = DataValidator.filterValid(playersParsed)
    val dedupPlayers = DataValidator.deduplicateById(validPlayers)
    
    val stats = StatsCalculator.ParsingStats(playersParsed, parsingErrors)
    
    val aggregationReport = AggregationReport(
      playersByLeague = StatsCalculator.countByLeague(dedupPlayers),
      playersByPosition = StatsCalculator.countByPosition(dedupPlayers),
      averageAgeByPosition = StatsCalculator.averageAgeByPosition(dedupPlayers),
      averageGoalsPerMatchByPosition = StatsCalculator.averageGoalsPerMatchByPosition(dedupPlayers)
    )
    val discipline = StatsCalculator.disciplineStats(dedupPlayers)

    val bonus = StatsCalculator.bonusReport(dedupPlayers)

    AnalysisReport(
      playerStats = stats,
      topScorers = StatsCalculator.top10Scorers(dedupPlayers),
      topAssisters = StatsCalculator.top10Assisters(dedupPlayers),
      topMarketValues = StatsCalculator.top10MarketValues(dedupPlayers),
      topSalaries = StatsCalculator.top10Salaries(dedupPlayers),
      aggregationReport = aggregationReport,
      disciplineStats = discipline,
      bonusReport = bonus
    )
  }


  def generateReportFromLoad(loadResult: Either[String, (List[Player], Int)]): Either[String, AnalysisReport] =
    loadResult match {
      case Left(err) => Left(err)
      case Right((players, parsingErrors)) =>
        Right(generateReport(players, parsingErrors))
    }

  def writeJsonReport(report: AnalysisReport, filename: String): Either[String, Unit] = {
    val jsonString = report.asJson.spaces2

    Try {
      Files.write(
        Paths.get(filename),
        jsonString.getBytes(StandardCharsets.UTF_8)
      )
      ()
    } match {
      case Success(_) => Right(())
      case Failure(ex) => Left(s"Write JSON error: ${ex.getMessage}")
    }
  }


  def writeTextReport(report: AnalysisReport, filename: String, durationSeconds: Double, throughput: Double): Either[String, Unit] = {

    def line(s: String) = s + "\n"

    val sb = new StringBuilder

    sb.append(line("==============================================="))
    sb.append(line("   RAPPORT D'ANALYSE - JOUEURS DE FOOTBALL"))
    sb.append(line("===============================================\n"))

    sb.append(line("📊 STATISTIQUES DE PARSING"))
    sb.append(line("---------------------------"))
    sb.append(line(f"- Entrées totales lues      : ${report.playerStats.total_players_parsed}%d"))
    sb.append(line(f"- Entrées valides           : ${report.playerStats.total_players_valid}%d"))
    sb.append(line(f"- Erreurs de parsing        : ${report.playerStats.parsing_errors}%d"))
    sb.append(line(f"- Doublons supprimés        : ${report.playerStats.duplicates_removed}%d\n"))

    sb.append(line("⚽ TOP 10 - BUTEURS"))
    sb.append(line("-------------------"))
    report.topScorers.zipWithIndex.foreach { case (t, i) =>
      sb.append(line(f"${i + 1}%2d. ${t.name}%-25s : ${t.goals}%d buts en ${t.matches}%d matchs"))
    }
    sb.append(line(""))

    sb.append(line("🎯 TOP 10 - PASSEURS"))
    sb.append(line("---------------------"))
    report.topAssisters.zipWithIndex.foreach { case (t, i) =>
      sb.append(line(f"${i + 1}%2d. ${t.name}%-25s : ${t.assists}%d passes en ${t.matches}%d matchs"))
    }
    sb.append(line(""))

    sb.append(line("💰 TOP 10 - VALEUR MARCHANDE"))
    sb.append(line("-----------------------------"))
    report.topMarketValues.zipWithIndex.foreach { case (t, i) =>
      sb.append(line(f"${i + 1}%2d. ${t.name}%-25s : ${t.marketValue}%.2f M€"))
    }
    sb.append(line(""))

    sb.append(line("💵 TOP 10 - SALAIRES"))
    sb.append(line("--------------------"))
    report.topSalaries.zipWithIndex.foreach { case (t, i) =>
      sb.append(line(f"${i + 1}%2d. ${t.name}%-25s : ${t.salary}%.2f M€/an"))
    }
    sb.append(line(""))

    sb.append(line("🏆 RÉPARTITION PAR LIGUE"))
    sb.append(line("-------------------------"))
    report.aggregationReport.playersByLeague.toList.sortBy(-_._2).foreach { case (league, count) =>
      sb.append(line(f"- ${league}%-25s : $count%d joueurs"))
    }
    sb.append(line(""))

    sb.append(line("⚽ RÉPARTITION PAR POSTE"))
    sb.append(line("------------------------"))
    report.aggregationReport.playersByPosition.toList.sortBy(-_._2).foreach { case (pos, count) =>
      sb.append(line(f"- ${pos}%-25s : $count%d joueurs"))
    }
    sb.append(line(""))

    sb.append(line("📊 MOYENNES PAR POSTE"))
    sb.append(line("----------------------"))
    sb.append(line("ÂGE MOYEN :"))
    report.aggregationReport.averageAgeByPosition.toList.sortBy(_._1).foreach { case (pos, avg) =>
      sb.append(line(f"- ${pos}%-25s : ${avg}%.2f ans"))
    }
    sb.append(line(""))
    sb.append(line("BUTS PAR MATCH (moyenne) :"))
    report.aggregationReport.averageGoalsPerMatchByPosition.toList.sortBy(-_._2).foreach { case (pos, avg) =>
      sb.append(line(f"- ${pos}%-25s : ${avg}%.3f buts"))
    }
    sb.append(line(""))

    sb.append(line("🟨 DISCIPLINE"))
    sb.append(line("--------------"))
    sb.append(line(f"- Total cartons jaunes      : ${report.disciplineStats.totalYellowCards}%d"))
    sb.append(line(f"- Total cartons rouges      : ${report.disciplineStats.totalRedCards}%d"))
    sb.append(line(s"- Poste le plus discipliné  : ${report.disciplineStats.mostDisciplinedPosition}"))
    sb.append(line(s"- Poste le moins discipliné : ${report.disciplineStats.leastDisciplinedPosition}\n"))

    sb.append(line("BONUS - EFFICACITÉ & QUALITÉ/PRIX"))
    sb.append(line("-----------------------------------"))

    sb.append(line("Top 10 efficacité (buts/match, min 10 matchs) :"))
    report.bonusReport.topEfficientPlayers.zipWithIndex.foreach { case (t, i) =>
      sb.append(line(f"${i + 1}%2d. ${t.name}%-25s | ${t.club}%-20s | ${t.goalsPerMatch}%.3f"))
    }
    sb.append(line(""))

    sb.append(line("Top 10 bonnes affaires (buts/salaire) :"))
    report.bonusReport.topValueForMoney.zipWithIndex.foreach { case (t, i) =>
      sb.append(line(f"${i + 1}%2d. ${t.name}%-25s | ${t.club}%-20s | ${t.goalsPerSalary}%.6f"))
    }
    sb.append(line(""))

    sb.append(line("Stats par ligue (âge moyen, buts moyens) :"))
    report.bonusReport.leagueStats.foreach { s =>
      sb.append(line(f"- ${s.league}%-25s : âge=${s.avgAge}%.2f | buts=${s.avgGoals}%.2f"))
    }
    sb.append(line(s"\nLigue la plus productive : ${report.bonusReport.mostProductiveLeague}\n"))


    sb.append(line("⏱️  PERFORMANCE"))
    sb.append(line("---------------"))
    sb.append(line(f"- Temps de traitement       : ${durationSeconds}%.3f secondes"))
    sb.append(line(f"- Entrées/seconde           : ${throughput}%.2f"))

    sb.append(line("\n==============================================="))

    val content = sb.toString()

    Try {
      Files.write(Paths.get(filename), content.getBytes(StandardCharsets.UTF_8))
      ()
    } match {
      case Success(_) => Right(())
      case Failure(ex) => Left(s"Write TXT error: ${ex.getMessage}")
    }
  }
}
