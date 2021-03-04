package no.nav.sbl.dialogarena.mininnboks.consumer

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import no.nav.sbl.dialogarena.mininnboks.TestUtils.dummySubject
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kommune
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DefaultPersonServiceTest {

    val personV3: PersonV3 = mockk()
    var personService: PersonService.Default = PersonService.Default(personV3)

    @Test
    fun `henter Enhet`() {
        val enhet = "1234"
        coEvery { personV3.hentGeografiskTilknytning(any()) } returns
            HentGeografiskTilknytningResponse().withGeografiskTilknytning(Kommune().withGeografiskTilknytning(enhet))
        runBlocking {
            assertThat(personService.hentGeografiskTilknytning(dummySubject()), Is.`is`(enhet))
        }
    }

    @Test
    fun `kaster Runtime Exception Om Enhet Ikke Kan hentes`() {
        assertThrows<RuntimeException> {
            verify {
                runBlocking {
                    personService.hentGeografiskTilknytning(any())
                }
            }
        }
    }
}
