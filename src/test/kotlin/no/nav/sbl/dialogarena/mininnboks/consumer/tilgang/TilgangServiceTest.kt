package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import com.nhaarman.mockitokotlin2.any
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
        every {personService.hentGeografiskTilknytning() } throws IllegalStateException()

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.FEILET)
        assertThat(result.melding).contains("brukers GT")
        verify(exactly = 0) {pdlService.harKode6(any())}
        verify(exactly = 0) {pdlService.harStrengtFortroligAdresse(any())}
    }

    @Test
    fun `gir INGEN_ENHET om bruker ikke har enhet`() {
        val (pdlService, personService, tilgangService) = gittContext()
        every {personService.hentGeografiskTilknytning() } returns Optional.empty()

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.INGEN_ENHET)
        assertThat(result.melding).contains("gyldig GT")
        verify(exactly = 0) {pdlService.harKode6(any())}
        verify(exactly = 0) {pdlService.harStrengtFortroligAdresse(any())}
    }

    @Test
    fun `gir INGEN_ENHET om bruker ikke har GT`() {
        val (pdlService, personService, tilgangService) = gittContext()
        every {personService.hentGeografiskTilknytning() } returns (Optional.empty())

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.INGEN_ENHET)
        assertThat(result.melding).contains("gyldig GT")
        verify(exactly = 0) {pdlService.harKode6(any())}
        verify(exactly = 0) {pdlService.harStrengtFortroligAdresse(any())}
    }

    @Test
    fun `gir INGEN_ENHET om bruker har GT utland`() {
        val (pdlService, personService, tilgangService) = gittContext()
        every {personService.hentGeografiskTilknytning() } returns (Optional.of("SWE"))

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.INGEN_ENHET)
        assertThat(result.melding).contains("gyldig GT")
        verify(exactly = 0) {pdlService.harKode6(any())}
        verify(exactly = 0) {pdlService.harStrengtFortroligAdresse(any())}
    }

    @Test
    fun `gir FEILET om pdl-api kall feiler`() {
        val (pdlService, personService, tilgangService) = gittContext()
        every {personService.hentGeografiskTilknytning() } .returns (Optional.of("0123"))
        every {pdlService.harKode6(any())} throws IllegalStateException()

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.FEILET)
        assertThat(result.melding).contains("brukers diskresjonskode")

    }

    @Test
    fun `gir KODE6 om bruker har kode6`() {
        val (pdlService, personService, tilgangService) = gittContext()
        every { personService.hentGeografiskTilknytning()}  returns (Optional.of("0123"))
        every { pdlService.harKode6(any()) } returns  true

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.KODE6)
        assertThat(result.melding).contains("diskresjonskode")
    }

    @Test
    fun `gir OK om bruker har har enhet og ikke kode6`() {
        val (pdlService, personService, tilgangService) = gittContext()
        every {personService.hentGeografiskTilknytning() } returns (Optional.of("0123"))
        every {pdlService.harKode6(any()) } returns false

        val result = tilgangService.harTilgangTilKommunalInnsending("anyfnr")

        assertThat(result.resultat).isEqualTo(TilgangDTO.Resultat.OK)
    }
    private fun gittContext(): MockContext {
        val pdlService = mockk<PdlService>()
        val personService = mockk<PersonService>()
        val tilgangService = TilgangServiceImpl(pdlService, personService)
        return MockContext(pdlService, personService, tilgangService)
    }
}
