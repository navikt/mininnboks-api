package no.nav.sbl.dialogarena.mininnboks.consumer

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.sbl.dialogarena.mininnboks.TestUtils.dummySubject
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kommune
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.spekframework.spek2.Spek

val personV3: PersonV3 = mockk()

object DefaultPersonServiceTest : Spek({

    var personService: PersonService.Default = PersonService.Default(personV3)

    test("henter Enhet") {
        val enhet = "1234"
        coEvery { personV3.hentGeografiskTilknytning(any()) } returns
            HentGeografiskTilknytningResponse().withGeografiskTilknytning(Kommune().withGeografiskTilknytning(enhet))
        runBlocking {
            assertThat(personService.hentGeografiskTilknytning(dummySubject()), Is.`is`(enhet))
        }
    }

    test("`kaster Runtime Exception Om Enhet Ikke Kan hentes") {
        coEvery { personV3.hentGeografiskTilknytning(any()) } throws RuntimeException("RuntimeException")
        try {
            runBlocking {
                personService.hentGeografiskTilknytning(dummySubject())
            }
        } catch (t: Throwable) {
            assertThat(t.message, Is.`is`("RuntimeException"))
        }
    }
})
