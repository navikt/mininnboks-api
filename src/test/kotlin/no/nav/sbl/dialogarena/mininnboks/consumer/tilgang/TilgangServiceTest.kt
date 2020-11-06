package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import java.util.*

internal data class MockContext(
        val pdlService: PdlService,
        val personService: PersonService,
        val tilgangService: TilgangService
)

class TilgangServiceTest {
    @Test
    suspend fun `gir FEILET om hentEnhet feiler`() {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(any()) } throws IllegalStateException()
        verify {
            runBlocking {
                val result = tilgangService.harTilgangTilKommunalInnsending(any())
                assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.FEILET))
                assertThat(result.melding, Matchers.`containsString`("brukers GT"))
                coVerify(exactly = 0) { pdlService.harKode6(any()) }
                coVerify(exactly = 0) { pdlService.harStrengtFortroligAdresse(any()) }
            }
        }
    }


    @Test
    suspend fun `gir INGEN_ENHET om bruker ikke har enhet`() {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(any()) } returns Optional.empty()
        verify {
            runBlocking {
                val result = tilgangService.harTilgangTilKommunalInnsending(any())
                assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.INGEN_ENHET))
                assertThat(result.melding, Matchers.`containsString`("gyldig GT"))
                coVerify(exactly = 0) { pdlService.harKode6(any()) }
                coVerify(exactly = 0) { pdlService.harStrengtFortroligAdresse(any()) }
            }
        }
    }

    @Test
    suspend fun `gir INGEN_ENHET om bruker ikke har GT`() {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(any()) } returns (Optional.empty())
        verify {
            runBlocking {
                val result = tilgangService.harTilgangTilKommunalInnsending(any())

                assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.INGEN_ENHET))
                assertThat(result.melding, Matchers.`containsString`("gyldig GT"))
                coVerify(exactly = 0) { pdlService.harKode6(any()) }
                coVerify(exactly = 0) { pdlService.harStrengtFortroligAdresse(any()) }
            }
        }
    }

    @Test
    suspend fun `gir INGEN_ENHET om bruker har GT utland`() {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(any()) } returns (Optional.of("SWE"))
        verify {
            runBlocking {
                val result = tilgangService.harTilgangTilKommunalInnsending(any())

                assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.INGEN_ENHET))
                assertThat(result.melding, Matchers.`containsString`("gyldig GT"))
                coVerify(exactly = 0) { pdlService.harKode6(any()) }
                coVerify(exactly = 0) { pdlService.harStrengtFortroligAdresse(any()) }
            }
        }
    }

    @Test
    suspend fun `gir FEILET om pdl-api kall feiler`() {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(any()) }.returns(Optional.of("0123"))
        coEvery { pdlService.harKode6(any()) } throws IllegalStateException()
        verify {
            runBlocking {
                val result = tilgangService.harTilgangTilKommunalInnsending(any())

                assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.FEILET))
                assertThat(result.melding, Matchers.`containsString`("brukers diskresjonskode"))
            }
        }
    }

    @Test
    suspend fun `gir KODE6 om bruker har kode6`() {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(any()) } returns (Optional.of("0123"))
        coEvery { pdlService.harKode6(any()) } returns true
        verify {
            runBlocking {
                val result = tilgangService.harTilgangTilKommunalInnsending(any())

                assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.KODE6))
                assertThat(result.melding, Matchers.`containsString`("diskresjonskode"))
            }
        }
    }

    @Test
    suspend fun `gir OK om bruker har har enhet og ikke kode6`() {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(any()) } returns (Optional.of("0123"))
        coEvery { pdlService.harKode6(any()) } returns false
        verify {
            runBlocking {
                val result = tilgangService.harTilgangTilKommunalInnsending(any())
                assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.OK))
            }
        }
    }

    private fun gittContext(): MockContext {
        val pdlService = mockk<PdlService>()
        val personService = mockk<PersonService>()
        val tilgangService = TilgangServiceImpl(pdlService, personService)
        return MockContext(pdlService, personService, tilgangService)
    }
}
