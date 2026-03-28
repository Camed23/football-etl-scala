package footballEtl

object Main extends App {

  println(" Football ETL : Analyse de joueurs\n")

  val startMs = System.currentTimeMillis()
  
  val result = for {
    loaded <- DataLoader.loadPlayer("data/data_large.json")
    (playersParsed, parsingErrors) = loaded
    _ = println(s"${playersParsed.length} joueurs parsés (succès)")
    _ = println(s"${parsingErrors} erreurs de parsing (éléments ignorés)")
    
    validPlayers = DataValidator.filterValid(playersParsed)
    dedupPlayers = DataValidator.deduplicateById(validPlayers)
    _ = println(s"${validPlayers.length} joueurs valides")
    _ = println(s"${dedupPlayers.length} joueurs après suppression des doublons")
    
    report = ReportGenerator.generateReport(playersParsed, parsingErrors)
    _ = println(s"Rapport généré")
    
    _ <- ReportGenerator.writeJsonReport(report, "output/results.json")
    _ = println(s"JSON écrit dans results.json")
    
    endMs = System.currentTimeMillis()
    durationSeconds = (endMs - startMs) / 1000.0
    throughput = if (durationSeconds <= 0) 0.0 else report.playerStats.total_players_parsed.toDouble / durationSeconds

    _ <- ReportGenerator.writeTextReport(report, "output/report.txt", durationSeconds, throughput)
    _ = println(s"TXT écrit dans report.txt")

  } yield (report, durationSeconds, throughput)
  
  result match {
    case Right(report, durationSeconds, throughput) =>
      println("\n STATISTIQUES DE PARSING")
      println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      println(s"- Entrées totales lues : ${report.playerStats.total_players_parsed}")
      println(s"- Entrées valides      : ${report.playerStats.total_players_valid}")
      println(s"- Erreurs de parsing   : ${report.playerStats.parsing_errors}")
      println(s"- Doublons supprimés   : ${report.playerStats.duplicates_removed}")

      println("\n PERFORMANCE")
      println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      println(f"- Temps d'exécution    : ${durationSeconds}%.3f secondes")
      println(f"- Débit                : ${throughput}%.2f entrées/seconde")

      println("\nPipeline ETL terminé avec succès !")

    case Left(error) =>
      println(s"\nERREUR : $error")
      println("Pipeline ETL interrompu.")
  }
}
