package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class MininnboksSimulation extends Simulation {

  final val ENV = System.getProperty("environment", "t11")
  val baseUrl = "https://tjenester-" + ENV + ".nav.no"
  val goTo = baseUrl + "/mininnboks/"
  val userCredentials = csv("brukere.csv").circular
  val password = "Eifel123"
  val nrUsers: Int = Integer.getInteger("nrUsers", 1)
  val rampTime: Long = java.lang.Long.getLong("rampTime", 1L)

  val httpConf = http
    .baseURL(baseUrl)
    .disableWarmUp

  val standard_headers = Map( """Accept""" -> """text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""")

  val ajaxHeaders = Map(
    """Accept""" -> """application/xml, text/xml, */*; q=0.01""",
    """Cache-Control""" -> """no-cache""",
    """Content-Type""" -> """application/x-www-form-urlencoded; charset=UTF-8""",
    """Pragma""" -> """no-cache""",
    """Wicket-Ajax""" -> """true""",
    """Wicket-Ajax-BaseURL""" -> """sporsmal/skriv/HJELPEMIDLER""",
    """X-Requested-With""" -> """XMLHttpRequest""")

  val scn = scenario("Scenario Name")
    .feed(userCredentials)

    .exec(http("går til loginsiden med riktig parametre")
    .get("https://tjenester-" + ENV + ".nav.no/esso/UI/Login?goto=https://tjenester-" + ENV + ".nav.no/mininnboks/&service=level4Service")
    .headers(standard_headers)
    .check(regex("OpenAM").exists))

    .exec(http("logger inn")
    .post("/esso/UI/Login")
    .headers(standard_headers)
    .param("IDToken1", "${brukernavn}")
    .param("IDToken2", password)
    .queryParam("goto", goTo)
    .check(regex("Min Innboks").exists))

    .exec(http("test om jeg er logget inn")
    .get( """/mininnboks/""")
    .headers(standard_headers)
    .check(regex("Min Innboks").exists))

    .exec(http("Tar en snarvei til siden der man skriver inn spørsmål")
    .get( """/mininnboks/sporsmal/skriv/HJELPEMIDLER""")
    .headers(standard_headers)
    .check(regex("Skriv melding").exists))

    .exec(http("Sender det faktiske spørsmålet")
    .post( """/mininnboks/sporsmal/skriv/HJELPEMIDLER?2-1.IBehaviorListener.0-sporsmal~form-send=""")
    .headers(ajaxHeaders)
    .param( """tekstfelt:text""", """Dette er en melding som er sendt av gatling. Er den ikke fin?""")
    .param( """send""", """1""")
    .check(regex("kvittering").exists))

    .exec(http("Logger ut")
    .get( """/esso/UI/Logout""")
    .headers(standard_headers)
    .check(regex("You are logged out").exists))


  setUp(scn.inject(ramp(nrUsers users) over(rampTime seconds))).protocols(httpConf)
}