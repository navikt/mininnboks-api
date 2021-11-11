package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.cio.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.Subject
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClient
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClientConfig
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries.HentAdressebeskyttelse
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries.HentFolkeregistrertAdresse
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries.HentGeografiskTilknytning
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.mininnboks.getOrThrowWith

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
    val type: Type
) {
    enum class Type {
        GATEADRESSE,
        MATRIKKELADRESSE
    }
}

open class PdlService(
    private val client: HttpClient,
    private val stsService: SystemuserTokenProvider,
    private val configuration: Configuration
) {
    private val pdlUrl: String = configuration.PDL_API_URL + "/graphql"
    private val graphqlClient = GraphQLClient(
        client,
        GraphQLClientConfig(
            tjenesteNavn = "PDL",
            requestConfig = { callId, subject ->
                val subjectToken = subject.ssoToken.token
                val appToken: String = stsService.getSystemUserAccessToken()
                    ?: throw IllegalStateException("Kunne ikke hente ut systemusertoken")

                url(pdlUrl)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Id", "mininnboks-api")
                header("Nav-Consumer-Token", "Bearer $appToken")
                header("Authorization", "Bearer $subjectToken")
                header("Tema", "GEN")
                header("x-nav-apiKey", configuration.PDL_API_APIKEY)
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
            .getOrThrowWith { PdlException("Kunne ikke utlede adressebeskyttelse", it) }
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
            .getOrThrowWith {
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
            .getOrThrowWith {
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
                kommunenummer = kommunenummer,
                postnummer = postnummer,
                type = Adresse.Type.GATEADRESSE
            )
        }
    }

    private fun tilAdresse(matrikkeladresse: HentFolkeregistrertAdresse.Matrikkeladresse): Adresse {
        return with(matrikkeladresse) {
            Adresse(
                adresse = "$bruksenhetsnummer $tilleggsnavn",
                postnummer = postnummer,
                kommunenummer = kommunenummer,
                type = Adresse.Type.MATRIKKELADRESSE
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
            return if (it == 200) {
                HealthCheckResult.healthy()
            } else {
                HealthCheckResult.unhealthy("Statuskode: $it")
            }
        }.onFailure {
            return HealthCheckResult.unhealthy(it.message)
        }

        return HealthCheckResult.unhealthy("Feil ved Helsesjekk")
    }

    private fun pingGraphQL(): Int {
        val response: Response = runBlocking {
            client.get<Response>(configuration.PDL_API_URL + "/graphql") {
                header("x-nav-apiKey", configuration.PDL_API_APIKEY)
            }
        }

        return if (response.status == 200) {
            response.status
        } else {
            response.close()
            response.status
        }
    }
}
