package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.nav.common.auth.subject.Subject
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries.HentAdressebeskyttelse
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries.HentFolkeregistrertAdresse
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries.HentGeografiskTilknytning
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class PdlException(message: String, cause: Throwable) : RuntimeException(message, cause)

data class Adresse(
    val adresse: String? = null,
    val tilleggsnavn: String? = null,
    val husnummer: String? = null,
    val husbokstav: String? = null,
    val kommunenummer: String? = null,
    val kommunenavn: String? = null,
    val postnummer: String? = null,
    val poststed: String? = null,
    val gatekode: String? = null,
    val bydel: String? = null,
    val geografiskTilknytning: String? = null,
    val type: String
)

open class PdlService(
    private val pdlClient: OkHttpClient,
    private val stsService: SystemuserTokenProvider,
    private val configuration: Configuration
) {
    private val pdlUrl: String = configuration.PDL_API_URL + "/graphql"
    private val graphqlClient = GraphQLClient(
        pdlClient,
        GraphQLClientConfig(
            tjenesteNavn = "PDL",
            requestConfig = { callId, subject ->
                val subjectToken = subject.ssoToken.token
                val appToken: String = stsService.getSystemUserAccessToken()
                    ?: throw IllegalStateException("Kunne ikke hente ut systemusertoken")

                url(pdlUrl)
                addHeader("Nav-Call-Id", callId)
                addHeader("Nav-Consumer-Id", "mininnboks-api")
                addHeader("Nav-Consumer-Token", "Bearer $appToken")
                addHeader("Authorization", "Bearer $subjectToken")
                addHeader("Tema", "GEN")
                addHeader("x-nav-apiKey", configuration.PDL_API_APIKEY)
            }
        )
    )

    val selfTestCheck: SelfTestCheck =
        SelfTestCheck("Henter adressebeskyttelse: $pdlUrl", false) {
            checkHealth()
        }

    suspend fun harStrengtFortroligAdresse(subject: Subject) = harKode6(subject)
    suspend fun harFortroligAdresse(subject: Subject) = harKode7(subject)
    suspend fun harKode6(subject: Subject): Boolean =
        harGradering(subject, HentAdressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG)

    suspend fun harKode7(subject: Subject): Boolean =
        harGradering(subject, HentAdressebeskyttelse.AdressebeskyttelseGradering.FORTROLIG)

    suspend fun hentAdresseBeskyttelse(subject: Subject): HentAdressebeskyttelse.AdressebeskyttelseGradering? {
        return graphqlClient
            .runCatching {
                execute(
                    subject,
                    HentAdressebeskyttelse(
                        HentAdressebeskyttelse.Variables(subject.uid)
                    )
                )
            }
            .map { response ->
                response
                    .data
                    ?.hentPerson
                    ?.adressebeskyttelse
                    ?.firstOrNull()
                    ?.gradering
            }
            .getOrThrow { PdlException("Kunne ikke utlede adressebeskyttelse", it) }
    }

    suspend fun hentGeografiskTilknytning(subject: Subject): String {
        return graphqlClient
            .runCatching {
                execute(
                    subject,
                    HentGeografiskTilknytning(HentGeografiskTilknytning.Variables(subject.uid))
                )
            }
            .map { response -> response.data?.hentGeografiskTilknytning }
            .map { gt ->
                when (gt?.gtType) {
                    HentGeografiskTilknytning.GtType.KOMMUNE -> requireNotNull(gt.gtKommune)
                    HentGeografiskTilknytning.GtType.BYDEL -> requireNotNull(gt.gtBydel)
                    else -> {
                        throw IllegalStateException("Ukjent GtType: ${gt?.gtType}")
                    }
                }
            }
            .getOrThrow {
                PdlException("Feil ved uthenting av GT", it)
            }
    }

    suspend fun hentFolkeregistrertAdresse(subject: Subject): Adresse? {
        return graphqlClient
            .runCatching {
                execute(
                    subject,
                    HentFolkeregistrertAdresse(HentFolkeregistrertAdresse.Variables(subject.uid))
                )
            }
            .map { response ->
                response.data?.hentPerson
            }
            .map { adresser -> lagAdresseListe(adresser) }
            .getOrThrow {
                PdlException("Feil ved uthenting av adresser", it)
            }
            .firstOrNull()
    }

    private fun lagAdresseListe(response: HentFolkeregistrertAdresse.Person?): List<Adresse> {
        if (response == null) {
            return emptyList()
        }
        return response.bostedsadresse.map(this::tilAdresse)
    }

    private fun tilAdresse(adresse: HentFolkeregistrertAdresse.Bostedsadresse): Adresse {
        return when {
            adresse.vegadresse != null -> tilAdresse(adresse.vegadresse)
            adresse.matrikkeladresse != null -> tilAdresse(adresse.matrikkeladresse)
            else -> {
                throw IllegalStateException("Ukjent bostedsadresse fra PDL (må være vegadresee eller matrikkeladresse)")
            }
        }
    }

    private fun tilAdresse(vegadresse: HentFolkeregistrertAdresse.Vegadresse): Adresse {

        return with(vegadresse) {
            Adresse(
                adresse = adressenavn,
                tilleggsnavn = "$bruksenhetsnummer $tilleggsnavn",
                husnummer = husnummer,
                husbokstav = husbokstav,
                kommunenavn = kommunenummer,
                postnummer = postnummer,
                type = "VEGADRESSE"
            )
        }
    }

    private fun tilAdresse(matrikkeladresse: HentFolkeregistrertAdresse.Matrikkeladresse): Adresse {
        return with(matrikkeladresse) {
            Adresse(
                adresse = "$bruksenhetsnummer $tilleggsnavn",
                postnummer = postnummer,
                kommunenummer = kommunenummer,
                type = "MATRIKKELADRESSE"
            )
        }
    }


    private suspend fun harGradering(
        subject: Subject,
        gradering: HentAdressebeskyttelse.AdressebeskyttelseGradering
    ): Boolean {
        val pdlGradering = hentAdresseBeskyttelse(subject)
        return gradering == pdlGradering
    }

    fun checkHealth(): HealthCheckResult {

        kotlin.runCatching {
            pingGraphQL()

        }.onSuccess {
            return if (it == 200)
                HealthCheckResult.healthy()
            else
                HealthCheckResult.unhealthy("Statuskode: $it")

        }.onFailure {
            return HealthCheckResult.unhealthy(it.message)
        }

        return HealthCheckResult.unhealthy("Feil ved Helsesjekk")
    }

    private fun pingGraphQL(): Int {
        val request: Request = Request.Builder()
            .url(configuration.PDL_API_URL + "/graphql")
            .method("OPTIONS", null)
            .addHeader("x-nav-apiKey", configuration.PDL_API_APIKEY)
            .build()

        val response: Response = pdlClient.newCall(request).execute()

        return if (response.isSuccessful) {
            response.code()
        } else {
            response.body()?.close()
            response.code()
        }
    }

    companion object {
        fun lastQueryFraFil(name: String): String {
            return GraphQLClient::class.java
                .getResource("/pdl/$name.graphql")
                .readText()
                .replace("[\n\r]", "")
        }
    }
}

private inline fun <T> Result<T>.getOrThrow(fn: (Throwable) -> Throwable): T {
    val exception = exceptionOrNull()
    if (exception != null) {
        throw fn(exception)
    }
    return getOrThrow()
}
