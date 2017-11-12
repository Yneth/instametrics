import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FollowerLoadTest extends Simulation {

  val url = "http://localhost:9555"
  val userName = "mrhales109"
  val testTimeSpecs = 60

  val httpProtocol = http
    .baseURL(url)
    .acceptHeader("application/json")
    .userAgentHeader("Gatling")

  val testScenario = scenario("LoadTest")
    .during(testTimeSpecs) {
      exec(GetFollowers.query)
    }

  setUp(
    testScenario.inject(atOnceUsers(1))
//        .throttle(
//          reachOps(10 in (60 seconds)),
//          holdFor(testTimeSpecs seconds)
//        )
      .protocols(httpProtocol)
  )

  object GetFollowers {
    val query = exec(http("GetFollowers")
      .get(s"/followers/?userName=$userName")
      .asJSON
      .check(jsonPath("$.length()"))
    )
  }

}