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
  val nrUsers: Int = Integer.getInteger("nrUsers", 10)
  val rampTime: Long = java.lang.Long.getLong("rampTime", 1L)

  val httpConf = http
    .baseURL(baseUrl)
    .acceptHeader("image/png,image/*;q=0.8,*/*;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("nb-no,nb;q=0.9,no-no;q=0.8,no;q=0.6,nn-no;q=0.5,nn;q=0.4,en-us;q=0.3,en;q=0.1")
    .connection("keep-alive")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
    .disableWarmUp

  val standard_headers = Map( """Accept""" -> """text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""")

  val headers_8 = Map(
    """Accept""" -> """application/xml, text/xml, */*; q=0.01""",
    """Cache-Control""" -> """no-cache""",
    """Content-Type""" -> """application/x-www-form-urlencoded; charset=UTF-8""",
    """Pragma""" -> """no-cache""",
    """Wicket-Ajax""" -> """true""",
    """Wicket-Ajax-BaseURL""" -> """sporsmal/skriv/HJELPEMIDLER""",
    """Wicket-FocusedElementId""" -> """id41""",
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
    .headers(headers_8)
    .param( """id40_hf_0""", """""")
    .param( """tekstfelt:text""", """Dette er en melding som er sendt av gatling. Er den ikke fin?""")
    .param( """send""", """1""")
    .check(regex("kvittering").exists))

    .exec(http("Logger ut")
    .get( """/esso/UI/Logout""")
    .headers(standard_headers)
    .check(regex("You are logged out").exists))


  setUp(scn.inject(ramp(nrUsers users) over(rampTime seconds))).protocols(httpConf)
}