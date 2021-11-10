package no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries

import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLClient.Companion.lastQueryFraFil
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLRequest
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLResult
import no.nav.sbl.dialogarena.mininnboks.consumer.GraphQLVariables

class HentFolkeregistrertAdresse(override val variables: Variables) :
    GraphQLRequest<HentFolkeregistrertAdresse.Variables, HentFolkeregistrertAdresse.Result> {
    override val query: String = lastQueryFraFil("pdl", "hentFolkeregistrertAdresse")
    override val expectedReturnType: Class<Result> = Result::class.java

    data class Variables(val ident: String) : GraphQLVariables
    data class Result(val hentPerson: Person?) : GraphQLResult

    data class Person(
        val bostedsadresse: List<Bostedsadresse>
    )

    data class Bostedsadresse(
        val vegadresse: Vegadresse?,
        val matrikkeladresse: Matrikkeladresse?
    )

    data class Vegadresse(
        val matrikkelId: Long?,
        val adressenavn: String?,
        val husnummer: String?,
        val husbokstav: String?,
        val tilleggsnavn: String?,
        val postnummer: String?,
        val kommunenummer: String?,
        val bruksenhetsnummer: String?
    )

    data class Matrikkeladresse(
        val matrikkelId: Long?,
        val postnummer: String?,
        val tilleggsnavn: String?,
        val kommunenummer: String?,
        val bruksenhetsnummer: String?
    )
}
