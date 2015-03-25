package simulations

import java.lang.Double._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class MininnboksSimulation extends Simulation {

  final val ENV = System.getProperty("environment")
  final val BASE_URL = "https://tjenester-" + ENV + ".nav.no"
  val password = "Eifel123"
  val users: Integer = Integer.getInteger("users")
  val duration: Double = valueOf(System.getProperty("duration"))

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
    .feed(csv("brukere.csv").random)

    .exec(
      http("logging in")
        .post("/esso/UI/Login")
        .headers(standard_headers)
        .formParam("IDToken1", "${brukernavn}")
        .formParam("IDToken2", password)
        .queryParam("service", "level4Service")
        .queryParam("goto", BASE_URL + "/mininnboks/"))

    .exec(
      http("check to see if logged in properly")
        .get( """/mininnboks/""")
        .headers(standard_headers)
        .check(regex("Din dialog med NAV").exists))
    .pause(2)

    .exec(
      http("take a shortcut to page for sending in question")
        .get( """/mininnboks/sporsmal/skriv/ARBD""")
        .headers(standard_headers)
        .check(regex("Skriv til oss").exists))

    .exec(
      http("accept terms")
        .post( """/mininnboks/sporsmal/skriv/ARBD?2-1.IBehaviorListener.0-sporsmalForm-betingelseValg-betingelserCheckbox=""")
        .headers(ajaxHeaders)
        .formParam( """betingelseValg:betingelserCheckbox""", """on"""))

    .exec(
      http("send question")
        .post( """/mininnboks/sporsmal/skriv/ARBD?2-1.IBehaviorListener.0-sporsmalForm-send=""")
        .headers(ajaxHeaders)
        .formParam( """id6_hf_0""", """""")
        .formParam( """temagruppe""", """0""")
        .formParam( """tekstfelt:text""", """Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolore excepturi quo tempore. Ab alias consectetur dolorem ducimus, ex exercitationem explicabo magni minima nobis omnis qui sapiente sequi soluta veniam voluptatibus?""")
        .formParam( """betingelseValg:betingelserCheckbox""", """on""")
        .formParam( """send""", """1""")
        .check(status.is(200)))

    .exec(
      http("seeing receipt page")
        .get( """/mininnboks/sporsmal/kvittering""")
        .headers(standard_headers)
        .check(regex("Takk, du vil få svar").exists))

    .exec(
      http("logging out")
        .get( """/esso/UI/Logout""")
        .headers(standard_headers)
        .check(regex("You are logged out").exists))

  setUp(scn.inject(rampUsers(users) over (duration minutes))).protocols(httpConf)
}