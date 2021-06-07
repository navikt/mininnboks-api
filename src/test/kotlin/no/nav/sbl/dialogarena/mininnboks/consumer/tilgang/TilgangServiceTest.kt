package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import no.nav.sbl.dialogarena.mininnboks.dummySubject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.spekframework.spek2.Spek

class TilgangServiceTest : Spek({

    test("gir FEILET om hentEnhet feiler") {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(dummySubject) } throws IllegalStateException()
        runBlocking {
            val result = tilgangService.harTilgangTilKommunalInnsending(dummySubject)
            assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.FEILET))
            assertThat(result.melding, Matchers.`containsString`("brukers GT"))
            coVerify(exactly = 0) { pdlService.harKode6(any()) }
            coVerify(exactly = 0) { pdlService.harStrengtFortroligAdresse(any()) }
        }
    }

    test("gir INGEN_ENHET om bruker ikke har enhet") {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(dummySubject) } returns null
        runBlocking {
            val result = tilgangService.harTilgangTilKommunalInnsending(dummySubject)
            assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.INGEN_ENHET))
            assertThat(result.melding, Matchers.`containsString`("gyldig GT"))
            coVerify(exactly = 0) { pdlService.harKode6(any()) }
            coVerify(exactly = 0) { pdlService.harStrengtFortroligAdresse(any()) }
        }
    }

    test("gir INGEN_ENHET om bruker ikke har GT") {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(dummySubject) } returns null
        runBlocking {
            val result = tilgangService.harTilgangTilKommunalInnsending(dummySubject)
            assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.INGEN_ENHET))
            assertThat(result.melding, Matchers.`containsString`("gyldig GT"))
            coVerify(exactly = 0) { pdlService.harKode6(any()) }
            coVerify(exactly = 0) { pdlService.harStrengtFortroligAdresse(any()) }
        }
    }

    test("gir INGEN_ENHET om bruker har GT utland") {
        val (pdlService, personService, tilgangService) = gittContext()
        runBlocking {
            coEvery { personService.hentGeografiskTilknytning(dummySubject) } returns "SWE"
            val result = tilgangService.harTilgangTilKommunalInnsending(dummySubject)
            assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.INGEN_ENHET))
            assertThat(result.melding, Matchers.`containsString`("gyldig"))
            coVerify(exactly = 0) { pdlService.harKode6(any()) }
            coVerify(exactly = 0) { pdlService.harStrengtFortroligAdresse(any()) }
        }
    }

    test("gir FEILET om pdl-api kall feiler") {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(dummySubject) } returns "0123"
        coEvery { pdlService.harKode6(any()) } throws IllegalStateException()
        runBlocking {
            val result = tilgangService.harTilgangTilKommunalInnsending(dummySubject)

            assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.FEILET))
            assertThat(result.melding, Matchers.`containsString`("brukers diskresjonskode"))
        }
    }

    test("gir KODE6 om bruker har kode6") {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(dummySubject) } returns "0123"
        coEvery { pdlService.harKode6(any()) } returns true
        runBlocking {
            val result = tilgangService.harTilgangTilKommunalInnsending(dummySubject)
            assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.KODE6))
            assertThat(result.melding, Matchers.`containsString`("diskresjonskode"))
        }
    }

    test("gir OK om bruker har har enhet og ikke kode6") {
        val (pdlService, personService, tilgangService) = gittContext()
        coEvery { personService.hentGeografiskTilknytning(dummySubject) } returns "0123"
        coEvery { pdlService.harKode6(any()) } returns false
        runBlocking {
            val result = tilgangService.harTilgangTilKommunalInnsending(dummySubject)
            assertThat(result.resultat, Matchers.`is`(TilgangDTO.Resultat.OK))
        }
    }
})

fun gittContext(): MockContext {
    val pdlService = mockk<PdlService>()
    val personService = mockk<PersonService>()
    val tilgangService = TilgangServiceImpl(pdlService, personService)
    return MockContext(pdlService, personService, tilgangService)
}

data class MockContext(
    val pdlService: PdlService,
    val personService: PersonService,
    val tilgangService: TilgangService
)
