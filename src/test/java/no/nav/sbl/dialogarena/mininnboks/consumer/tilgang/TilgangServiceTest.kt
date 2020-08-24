package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import com.nhaarman.mockitokotlin2.*
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import java.util.*

internal data class MockContext(
        val pdlService: PdlService,
        val personService: PersonService,
        val tilgangService: TilgangService
)

class TilgangServiceTest {
    @Test
    fun `gir FEILET om hentEnhet feiler`() {
        val (pdlService, personService, tilgangService) = gittContext()
        whenever(personService.hentGeografiskTilknytning()).thenThrow(IllegalStateException::class.java)

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.FEILET)
        assertThat(result.melding).contains("brukers GT")
        verify(pdlService, never()).harKode6(any())
        verify(pdlService, never()).harStrengtFortroligAdresse(any())
    }

    @Test
    fun `gir INGEN_ENHET om bruker ikke har enhet`() {
        val (pdlService, personService, tilgangService) = gittContext()
        whenever(personService.hentGeografiskTilknytning()).thenReturn(Optional.empty())

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.INGEN_ENHET)
        assertThat(result.melding).contains("gyldig GT")
        verify(pdlService, never()).harKode6(any())
        verify(pdlService, never()).harStrengtFortroligAdresse(any())
    }

    @Test
    fun `gir INGEN_ENHET om bruker ikke har GT`() {
        val (pdlService, personService, tilgangService) = gittContext()
        whenever(personService.hentGeografiskTilknytning()).thenReturn(Optional.empty())

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.INGEN_ENHET)
        assertThat(result.melding).contains("gyldig GT")
        verify(pdlService, never()).harKode6(any())
        verify(pdlService, never()).harStrengtFortroligAdresse(any())
    }

    @Test
    fun `gir INGEN_ENHET om bruker har GT utland`() {
        val (pdlService, personService, tilgangService) = gittContext()
        whenever(personService.hentGeografiskTilknytning()).thenReturn(Optional.of("SWE"))

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.INGEN_ENHET)
        assertThat(result.melding).contains("gyldig GT")
        verify(pdlService, never()).harKode6(any())
        verify(pdlService, never()).harStrengtFortroligAdresse(any())
    }

    @Test
    fun `gir FEILET om pdl-api kall feiler`() {
        val (pdlService, personService, tilgangService) = gittContext()
        whenever(personService.hentGeografiskTilknytning()).thenReturn(Optional.of("0123"))
        whenever(pdlService.harKode6(any())).thenThrow(IllegalStateException::class.java)

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.FEILET)
        assertThat(result.melding).contains("brukers diskresjonskode")

    }

    @Test
    fun `gir KODE6 om bruker har kode6`() {
        val (pdlService, personService, tilgangService) = gittContext()
        whenever(personService.hentGeografiskTilknytning()).thenReturn(Optional.of("0123"))
        whenever(pdlService.harKode6(any())).thenReturn(true)

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.KODE6)
        assertThat(result.melding).contains("diskresjonskode")
    }

    @Test
    fun `gir OK om bruker har har enhet og ikke kode6`() {
        val (pdlService, personService, tilgangService) = gittContext()
        whenever(personService.hentGeografiskTilknytning()).thenReturn(Optional.of("0123"))
        whenever(pdlService.harKode6(any())).thenReturn(false)

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.OK)
    }

    private fun gittContext(): MockContext {
        val pdlService = mock<PdlService>()
        val personService = mock<PersonService>()
        val tilgangService = TilgangServiceImpl(pdlService, personService)
        return MockContext(pdlService, personService, tilgangService)
    }
}
