package no.nav.sbl.dialogarena.mininnboks.consumer.domain

data class Sporsmal(
    val temagruppe: Temagruppe,
    val fritekst: String,
    val overstyrtGt: String? = null
)
