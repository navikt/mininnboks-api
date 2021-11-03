package no.nav.sbl.dialogarena.mininnboks.consumer.saf

import io.ktor.client.*
import io.ktor.client.request.*
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClient
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClientConfig
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.TokendingsService

interface SafService {
    interface DokumentMetadata
    fun hentMetadata(): DokumentMetadata
    fun hentDokument(fnr: String, journalpostId: String, dokumentreferanse: String): ByteArray
}

class SafServiceImpl(
    private val client: HttpClient,
    private val tokendings: TokendingsService,
    private val configuration: Configuration
) : SafService {
    private val safUrl: String = configuration.SAF_API_URL + "/graphql"
    private val graphqlClient = GraphQLClient(
        client,
        GraphQLClientConfig(
            tjenesteNavn = "SAF",
            requestConfig = { callId, subject ->
                val token = tokendings.exchangeToken(subject.ssoToken.token, configuration.SAF_CLIENT_ID)

                url(safUrl)
                header("Nav-Call-Id", callId)
                header("Authorization", "Bearer $token")
                header("x-nav-apiKey", configuration.SAF_GRAPHQL_API_APIKEY)
            }
        )
    )

    override fun hentMetadata(): SafService.DokumentMetadata {
        TODO("Not yet implemented")
    }

    override fun hentDokument(fnr: String, journalpostId: String, dokumentreferanse: String): ByteArray {
        TODO("Not yet implemented")
    }
}
