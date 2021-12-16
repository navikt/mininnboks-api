package no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClient.Companion.lastQueryFraFil
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLRequest
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLResult
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLVariables

data class HentAdressebeskyttelse(override val variables: Variables) :
    GraphQLRequest<HentAdressebeskyttelse.Variables, HentAdressebeskyttelse.Result> {
    override val query: String = lastQueryFraFil("pdl", "hentAdressebeskyttelse")
    override val expectedReturnType: Class<Result> = Result::class.java

    data class Variables(val ident: String) : GraphQLVariables
    data class Result(val hentPerson: Person?) : GraphQLResult

    data class Person(
        val adressebeskyttelse: List<Adressebeskyttelse>
    )

    data class Adressebeskyttelse(
        val gradering: AdressebeskyttelseGradering
    )

    enum class AdressebeskyttelseGradering {
        STRENGT_FORTROLIG_UTLAND,

        STRENGT_FORTROLIG,

        FORTROLIG,

        UGRADERT,

        /**
         * This is a default enum value that will be used when attempting to deserialize unknown value.
         */
        @JsonEnumDefaultValue
        __UNKNOWN_VALUE
    }
}
