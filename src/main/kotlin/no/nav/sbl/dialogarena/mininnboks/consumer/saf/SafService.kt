package no.nav.sbl.dialogarena.mininnboks.consumer.saf

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.common.auth.subject.Subject
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClient
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClientConfig
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.queries.HentDokumentdata
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.queries.HentDokumentdata.AvsenderMottakerIdType.FNR
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.queries.HentDokumentdata.Datotype.*
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.queries.HentDokumentdata.VariantFormat.ARKIV
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.TokendingsService
import no.nav.sbl.dialogarena.mininnboks.getOrThrowWith
import org.slf4j.MDC
import java.time.LocalDateTime
import java.util.*

interface SafService {
    suspend fun hentJournalposter(subject: Subject): List<Journalpost>
    suspend fun hentJournalpost(subject: Subject, journalpostId: String, dokumentIdListe: List<String>): Journalpost?
    suspend fun hentDokument(subject: Subject, journalpostId: String, dokumentId: String): ByteArray

    data class Journalpost(
        val journalpostId: String,
        val tittel: String?,
        val dato: LocalDateTime,
        val retning: Retning,
        val avsender: Entitet,
        val mottaker: Entitet,
        val tema: String?,
        val dokumenter: List<Dokument>
    )

    data class Dokument(
        val dokumentId: String,
        val tittel: String?,
        val harTilgang: Boolean
    )

    enum class Retning {
        INN, UT, INTERN
    }
    enum class Entitet {
        NAV, SLUTTBRUKER, EKSTERN_PART, UKJENT
    }
}

class SafException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
class SafServiceImpl(
    private val client: HttpClient,
    private val tokendings: TokendingsService,
    private val configuration: Configuration
) : SafService {
    private val graphqlClient = GraphQLClient(
        client,
        GraphQLClientConfig(
            tjenesteNavn = "SAF",
            requestConfig = { callId, subject ->
                val token = tokendings.exchangeToken(subject.ssoToken.token, configuration.SAF_CLIENT_ID)

                url(configuration.SAF_API_URL + "/graphql")
                header("Nav-Call-Id", callId)
                header("Authorization", "Bearer $token")
                header("x-nav-apiKey", configuration.SAF_GRAPHQL_API_APIKEY)
            }
        )
    )

    override suspend fun hentJournalposter(subject: Subject): List<SafService.Journalpost> {
        val ident = subject.uid
        return withContext(Dispatchers.IO) {
            graphqlClient
                .runCatching {
                    execute(
                        subject,
                        HentDokumentdata(
                            HentDokumentdata.Variables(ident, emptyList())
                        )
                    )
                }
                .map { response ->
                    response
                        .data
                        ?.dokumentoversiktSelvbetjening
                        ?: throw IllegalStateException(
                            """
                            Uthenting av dokumentoversikt returnerte null og med følgende feil: 
                            ${response.errors?.joinToString("\n") { it.message }}
                            """.trimIndent()
                        )
                }
                .map { dokumentOversikt ->
                    dokumentOversikt.journalposter
                        .filterNotNull()
                        .map { journalpost ->
                            SafService.Journalpost(
                                journalpostId = journalpost.journalpostId,
                                tittel = journalpost.tittel,
                                dato = getDato(journalpost),
                                retning = getRetning(journalpost),
                                avsender = getAvsender(journalpost),
                                mottaker = getMottaker(journalpost),
                                tema = journalpost.tema,
                                dokumenter = journalpost.dokumenter
                                    ?.filterNotNull()
                                    ?.map { dokumentInfo ->
                                        SafService.Dokument(
                                            dokumentId = dokumentInfo.dokumentInfoId,
                                            tittel = dokumentInfo.tittel,
                                            harTilgang = dokumentInfo.dokumentvarianter
                                                .filterNotNull()
                                                .any { variant ->
                                                    variant.brukerHarTilgang && variant.variantformat == ARKIV
                                                }
                                        )
                                    }
                                    ?: emptyList()
                            )
                        }
                }
                .getOrThrowWith { SafException("Uthenting av dokumentoversikt feilet", it) }
        }
    }

    override suspend fun hentJournalpost(subject: Subject, journalpostId: String, dokumentIdListe: List<String>): SafService.Journalpost? {
        val journalposter = hentJournalposter(subject)

        val journalpost = journalposter
            .find { it.journalpostId == journalpostId }
        val dokumenter = (journalpost?.dokumenter ?: emptyList())
            .filter { dokumentIdListe.contains(it.dokumentId) }

        return journalpost?.copy(dokumenter = dokumenter)
    }

    override suspend fun hentDokument(subject: Subject, journalpostId: String, dokumentId: String): ByteArray {
        return withContext(Dispatchers.IO) {
            val token = tokendings.exchangeToken(subject.ssoToken.token, configuration.SAF_CLIENT_ID)
            val response: HttpResponse = runCatching {
                client.request<HttpResponse> {
                    url(configuration.SAF_API_URL + "/rest_hentdokument/$journalpostId/$dokumentId/ARKIV")
                    method = HttpMethod.Get
                    header("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString())
                    header("Authorization", "Bearer $token")
                    header("x-nav-apiKey", configuration.SAF_GRAPHQL_API_APIKEY)
                }
            }.onFailure { cause ->
                if (cause is ClientRequestException && cause.response.status == HttpStatusCode.NotFound) {
                    throw SafException("Fant ikke dokument. $journalpostId $dokumentId", cause)
                } else {
                    throw SafException("Klarte ikke å hente dokument. $journalpostId $dokumentId", cause)
                }
            }.getOrThrow()

            runCatching {
                response.readBytes()
            }.onFailure { cause ->
                throw SafException("Klarte ikke å lese inn dataene i responsen fra SAF", cause)
            }.getOrThrow()
        }
    }

    private fun getDato(journalpost: HentDokumentdata.Journalpost): LocalDateTime {
        return when (journalpost.journalposttype) {
            HentDokumentdata.Journalposttype.I -> journalpost.finnRelevantDato(DATO_REGISTRERT)
            HentDokumentdata.Journalposttype.U -> journalpost.finnRelevantDato(DATO_EKSPEDERT, DATO_SENDT_PRINT, DATO_JOURNALFOERT)
            HentDokumentdata.Journalposttype.N -> journalpost.finnRelevantDato(DATO_JOURNALFOERT)
            else -> LocalDateTime.now()
        } ?: LocalDateTime.now()
    }

    private fun getRetning(journalpost: HentDokumentdata.Journalpost): SafService.Retning {
        return when (journalpost.journalposttype) {
            HentDokumentdata.Journalposttype.I -> SafService.Retning.INN
            HentDokumentdata.Journalposttype.U -> SafService.Retning.UT
            HentDokumentdata.Journalposttype.N -> SafService.Retning.INTERN
            else -> throw SafException("Ukjent journalposttype ${journalpost.journalposttype}")
        }
    }

    private fun getAvsender(journalpost: HentDokumentdata.Journalpost): SafService.Entitet {
        return when (journalpost.journalposttype) {
            HentDokumentdata.Journalposttype.N -> SafService.Entitet.NAV
            HentDokumentdata.Journalposttype.U -> SafService.Entitet.NAV
            HentDokumentdata.Journalposttype.I -> if (journalpost.avsender?.type == FNR) SafService.Entitet.SLUTTBRUKER else SafService.Entitet.EKSTERN_PART
            else -> SafService.Entitet.UKJENT
        }
    }

    private fun getMottaker(journalpost: HentDokumentdata.Journalpost): SafService.Entitet {
        return when (journalpost.journalposttype) {
            HentDokumentdata.Journalposttype.I -> SafService.Entitet.NAV
            HentDokumentdata.Journalposttype.N -> SafService.Entitet.NAV
            HentDokumentdata.Journalposttype.U -> if (journalpost.mottaker?.type == FNR) SafService.Entitet.SLUTTBRUKER else SafService.Entitet.EKSTERN_PART
            else -> SafService.Entitet.UKJENT
        }
    }

    private fun HentDokumentdata.Journalpost.finnRelevantDato(vararg datotyper: HentDokumentdata.Datotype): LocalDateTime? {
        return this
            .relevanteDatoer
            .filterNotNull()
            .find { datotyper.contains(it.datotype) }
            ?.dato
    }
}
