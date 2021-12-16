package no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClient.Companion.lastQueryFraFil
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLRequest
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLResult
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLVariables

data class HentGeografiskTilknytning(override val variables: Variables) :
    GraphQLRequest<HentGeografiskTilknytning.Variables, HentGeografiskTilknytning.Result> {
    override val query: String = lastQueryFraFil("pdl", "hentGeografiskTilknytning")
    override val expectedReturnType: Class<Result> = Result::class.java

    data class Variables(val ident: String) : GraphQLVariables
    data class Result(val hentGeografiskTilknytning: GeografiskTilknytning?) : GraphQLResult

    data class GeografiskTilknytning(
        val gtType: GtType,
        val gtLand: String?,
        val gtKommune: String?,
        val gtBydel: String?
    )

    enum class GtType {
        KOMMUNE,
        BYDEL,
        UTLAND,
        UDEFINERT,

        /**
         * This is a default enum value that will be used when attempting to deserialize unknown value.
         */
        @JsonEnumDefaultValue
        __UNKNOWN_VALUE
    }
}
