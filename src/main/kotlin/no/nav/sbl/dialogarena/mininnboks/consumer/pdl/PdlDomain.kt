package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

data class PdlRequest(val query: String, val variables: Variables)

data class Variables(val ident: String)

data class PdlResponse(
        val errors: List<PdlError>?,
        val data: PdlHentPerson?
)


data class PdlHentPerson(
        val hentPerson: PdlPerson?
)

data class PdlPerson(
        val adressebeskyttelse: List<PdlAdressebeskyttelse>
)

data class PdlAdressebeskyttelse(
        val gradering: PdlAdressebeskyttelseGradering
)

enum class PdlAdressebeskyttelseGradering {
    STRENGT_FORTROLIG,
    FORTROLIG,
    UGRADERT
}

data class PdlError(
        val message: String
)
