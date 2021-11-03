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
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.TokendingsService
import no.nav.sbl.dialogarena.mininnboks.getOrThrowWith
import org.slf4j.MDC
import java.util.*

interface SafService {
    suspend fun hentDokumentoversikt(subject: Subject): HentDokumentdata.DokumentOversikt
    suspend fun hentMetadata(subject: Subject, journalpostId: String, dokumentIdListe: List<String>): HentDokumentdata.DokumentOversikt
    suspend fun hentDokument(subject: Subject, journalpostId: String, dokumentId: String): ByteArray
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

    override suspend fun hentDokumentoversikt(subject: Subject): HentDokumentdata.DokumentOversikt {
        val ident = subject.uid
        return withContext(Dispatchers.IO) {
            graphqlClient
                .runCatching {
                    execute(
                        subject,
                        HentDokumentdata(
                            HentDokumentdata.Variables(ident)
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
                .getOrThrowWith { SafException("Uthenting av dokumentoversikt feilet", it) }
        }
    }

    override suspend fun hentMetadata(subject: Subject, journalpostId: String, dokumentIdListe: List<String>): HentDokumentdata.DokumentOversikt {
        val dokumentoversikt = hentDokumentoversikt(subject)
        val journalpost = requireNotNull(dokumentoversikt.journalposter.find { it.journalpostId == journalpostId })
        val dokumenter = journalpost.dokumenter.filter { dokumentIdListe.contains(it.dokumentInfoId) }
        return dokumentoversikt.copy(
            journalposter = listOf(
                journalpost.copy(
                    dokumenter = dokumenter
                )
            )
        )
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
}
