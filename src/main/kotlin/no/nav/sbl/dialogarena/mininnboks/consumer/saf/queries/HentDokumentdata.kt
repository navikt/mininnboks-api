package no.nav.sbl.dialogarena.mininnboks.consumer.saf.queries

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClient.Companion.lastQueryFraFil
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLRequest
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLResult
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLVariables

class HentDokumentdata(override val variables: Variables) :
    GraphQLRequest<HentDokumentdata.Variables, HentDokumentdata.Result> {
    override val query: String = lastQueryFraFil("saf", "hentDokumentdata")
    override val expectedReturnType: Class<Result> = Result::class.java

    data class Variables(val ident: String) : GraphQLVariables
    data class Result(val dokumentoversiktSelvbetjening: DokumentOversikt) : GraphQLResult

    data class DokumentOversikt(val journalposter: List<Journalpost>)
    data class Journalpost(
        val journalpostId: String,
        val tittel: String?,
        val journalposttype: Journalposttype,
        val tema: String?,
        val avsender: AvsenderMottaker,
        val mottaker: AvsenderMottaker,
        val dokumenter: List<DokumentInfo>
    )

    enum class Journalposttype {
        I, U, N,

        @JsonEnumDefaultValue
        __UNKNOWN_VALUE
    }
    data class AvsenderMottaker(
        val id: String,
        val type: AvsenderMottakerIdType
    )
    enum class AvsenderMottakerIdType {
        FNR, ORGNR, HPRNR, UTL_ORG, NULL, UKJENT,

        @JsonEnumDefaultValue
        __UNKNOWN_VALUE
    }
    data class DokumentInfo(
        val dokumentInfoId: String,
        val tittel: String?,
        val dokumentvarianter: List<Dokumentvariant>
    )
    data class Dokumentvariant(
        val variantformat: VariantFormat,
        val brukerHarTilgang: Boolean
    )
    enum class VariantFormat {
        ARKIV, SLADDET,

        @JsonEnumDefaultValue
        __UNKNOWN_VALUE
    }
}
