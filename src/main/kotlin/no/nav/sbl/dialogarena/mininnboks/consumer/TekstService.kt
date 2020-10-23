package no.nav.sbl.dialogarena.mininnboks.consumer

interface TekstService {
    fun hentTekst(key: String): String?
    fun hentTekster(): Map<String, String?>
}
