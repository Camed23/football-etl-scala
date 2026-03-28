package footballEtl

case class Player(
                   id: Int,
                   name: String,
                   age: Int,
                   nationality: String,
                   position: String,
                   club: String,
                   league: String,
                   goalsScored: Int,
                   assists: Int,
                   matchesPlayed: Int,
                   yellowCards: Int,
                   redCards: Int,
                   marketValue: Option[Double],
                   salary: Option[Double]
                 )

case class PlayerStats(
                        total_players_parsed: Int,
                        total_players_valid: Int,
                        parsing_errors: Int,
                        duplicates_removed: Int
                      )

case class TopScorer(
                    name: String,
                    club: String,
                    goals: Int,
                    matches: Int
                    )

case class TopAssister(
                        name: String,
                        club: String,
                        assists: Int,
                        matches: Int
                      )

case class TopMarketValue(
                           name: String,
                           club: String,
                           marketValue: Double
                         )

case class TopSalary(
                      name: String,
                      club: String,
                      salary: Double
                    )

case class AggregationReport(
                              playersByLeague: Map[String, Int],
                              playersByPosition: Map[String, Int],
                              averageAgeByPosition: Map[String, Double],
                              averageGoalsPerMatchByPosition: Map[String, Double]
                            )

case class DisciplineStats(
                            totalYellowCards: Int,
                            totalRedCards: Int,
                            mostDisciplinedPosition: String,
                            leastDisciplinedPosition: String
                          )

case class AnalysisReport(
                           playerStats: PlayerStats,
                           topScorers: List[TopScorer],
                           topAssisters: List[TopAssister],
                           topMarketValues: List[TopMarketValue],
                           topSalaries: List[TopSalary],
                           aggregationReport: AggregationReport,
                           disciplineStats: DisciplineStats,
                           bonusReport: BonusReport
                         )

// Partie concernant le bonus

case class TopEfficiency(
                          name: String,
                          club: String,
                          goals: Int,
                          matches: Int,
                          goalsPerMatch: Double
                        )

case class TopValueForMoney(
                             name: String,
                             club: String,
                             goals: Int,
                             salary: Double,
                             goalsPerSalary: Double
                           )

case class LeagueBonusStats(
                             league: String,
                             avgAge: Double,
                             avgGoals: Double
                           )

case class BonusReport(
                        topEfficientPlayers: List[TopEfficiency],
                        topValueForMoney: List[TopValueForMoney],
                        leagueStats: List[LeagueBonusStats],
                        mostProductiveLeague: String
                      )

