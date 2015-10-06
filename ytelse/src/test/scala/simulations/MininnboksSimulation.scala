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

  val headers = Map( """Accept""" -> """text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""")

  val scn = scenario("Scenario Name")
    .feed(csv("brukere_" + ENV + ".csv").random)

    .exec(
      http("logging in")
        .post("/esso/UI/Login")
        .headers(headers)
        .formParam("IDToken1", "${brukernavn}")
        .formParam("IDToken2", password)
        .queryParam("service", "level4Service")
        .queryParam("goto", BASE_URL + "/mininnboks/"))

    .exec(
      http("check to see if logged in properly")
        .get( """/mininnboks/""")
        .headers(headers)
        .check(regex("mininnboks-omslag").exists))
    .pause(2)

    .exec(
      http("fetch resources")
        .get("/mininnboks/tjenester/resources")
        .headers(headers)
        .check(headerRegex("Set-Cookie", "XSRF-TOKEN-MININNBOKS=([^;]+)").saveAs("xsrfCookie")))

    .exec(
      http("send question")
        .post("/mininnboks/tjenester/traader/sporsmal")
        .headers(headers)
        .header("X-XSRF-TOKEN", "${xsrfCookie}")
        .body(StringBody( """{"temagruppe":"ARBD","fritekst":"Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolore excepturi quo tempore. Ab alias consectetur dolorem ducimus, ex exercitationem explicabo magni minima nobis omnis qui sapiente sequi soluta veniam voluptatibus?"}""")).asJSON)

    .exec(
      http("logging out")
        .get("""/esso/UI/Logout""")
        .headers(headers)
        .check(regex("You are logged out").exists))

  val httpProtocol = http
    .baseURL("http://localhost:8585")
    .inferHtmlResources()
    .acceptHeader("image/png,image/*;q=0.8,*/*;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("nb-no,nb;q=0.9,no-no;q=0.8,no;q=0.6,nn-no;q=0.5,nn;q=0.4,en-us;q=0.3,en;q=0.1")
    .connection("keep-alive")
    .contentTypeHeader("application/json; charset=UTF-8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")

  setUp(scn.inject(rampUsers(users) over (duration minutes))).protocols(httpConf)
}