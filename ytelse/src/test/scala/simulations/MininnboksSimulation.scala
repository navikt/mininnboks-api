package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class MininnboksSimulation extends Simulation {

  final val ENV = System.getProperty("environment", "t11")
  final val BASE_URL = "https://tjenester-" + ENV + ".nav.no"
  val userCredentials = csv("brukere.csv").circular
  val password = "Eifel123"
  // System.getProperty???
  val nrUsers: Int = Integer.getInteger("nrUsers", 1)
  val rampTime: Long = java.lang.Long.getLong("rampTime", 1L)

  val httpConf = http
    .baseURL(BASE_URL)
    .disableWarmUp

  val standard_headers = Map( """Accept""" -> """text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""")

  val ajaxHeaders = Map(
    """Accept""" -> """application/xml, text/xml, */*; q=0.01""",
    """Cache-Control""" -> """no-cache""",
    """Content-Type""" -> """application/x-www-form-urlencoded; charset=UTF-8""",
    """Pragma""" -> """no-cache""",
    """Wicket-Ajax""" -> """true""",
    """Wicket-Ajax-BaseURL""" -> """sporsmal/skriv/ARBD""",
    """X-Requested-With""" -> """XMLHttpRequest""")

  val scn = scenario("Scenario Name")
    .feed(userCredentials)

    .exec(
      http("Go to login page with correct parameters")
        .get("https://tjenester-" + ENV + ".nav.no/esso/UI/Login?goto=https://tjenester-" + ENV + ".nav.no/mininnboks/&service=level4Service")
        .headers(standard_headers)
        .check(regex("OpenAM").exists))

    .exec(
      http("logging in")
        .post("/esso/UI/Login")
        .headers(standard_headers)
        .formParam("IDToken1", "${brukernavn}")
        .formParam("IDToken2", password)
        .queryParam("goto", BASE_URL + "/mininnboks/")
        .check(regex("Min Innboks").exists))

    .exec(
      http("check to see if logged in properly")
        .get( """/mininnboks/""")
        .headers(standard_headers)
        .check(regex("Min Innboks").exists))

    .exec(
      http("take a shortcut to page for sending in question")
        .get( """/mininnboks/sporsmal/skriv/ARBD""")
        .headers(standard_headers)
        .check(regex("Skriv melding").exists))

    .exec(
      http("sending the actual question")
        .post( """/mininnboks/sporsmal/skriv/ARBD?2-1.IBehaviorListener.0-sporsmal~form-send=""")
        .headers(ajaxHeaders)
        .formParam( """tekstfelt:text""", """Dette er en melding som er sendt av gatling. Er den ikke fin?""")
        .formParam( """send""", """1""")
        .check(regex("kvittering").exists))

    .exec(http("seeing receipt page")
    .get( """/mininnboks/sporsmal/kvittering""")
    .headers(standard_headers)
    .check(regex("du vil få svar ").exists))

    .exec(http("logging out")
    .get( """/esso/UI/Logout""")
    .headers(standard_headers)
    .check(regex("You are logged out").exists))

  setUp(scn.inject(rampUsers(nrUsers) over (rampTime seconds))).protocols(httpConf)
}