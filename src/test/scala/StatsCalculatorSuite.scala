package footballEtl

import munit.FunSuite

// Just some tests

class StatsCalculatorSuite extends FunSuite {

  val p1 = Player(1, "A", 20, "FR", "Forward", "Club1", "Ligue 1", 10, 2, 10, 1, 0, Some(50.0), Some(5.0))
  val p2 = Player(2, "B", 30, "ES", "Forward", "Club2", "La Liga", 5, 1, 20, 0, 1, None, None)
  val p3 = Player(3, "C", 25, "DE", "Midfielder", "Club3", "Bundesliga", 0, 5, 12, 2, 0, Some(30.0), Some(3.0))

  test("top10Efficiency should filter players with less than 10 matches") {
    val res = StatsCalculator.top10Efficiency(List(p1, p2, p3), minMatches = 10)
    assert(res.exists(_.name == "A"))
    assert(res.exists(_.name == "C"))
  }


  test("top10ValueForMoney should ignore players without salary") {
    val res = StatsCalculator.top10ValueForMoney(List(p1, p2, p3))
    assert(res.forall(_.salary > 0))
    assert(!res.exists(_.name == "B"))
  }

  test("leagueStats should compute avgAge and avgGoals per league") {
    val res = StatsCalculator.leagueStats(List(p1, p2))
    val ligue1 = res.find(_.league == "Ligue 1").get
    assertEqualsDouble(ligue1.avgAge, 20.0, 0.0001)
    assertEqualsDouble(ligue1.avgGoals, 10.0, 0.0001)
  }

  test("mostProductiveLeague should return league with highest avgGoals") {
    val league = StatsCalculator.mostProductiveLeague(List(p1, p2))
    assertEquals(league, "Ligue 1")
  }
}
