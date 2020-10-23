package no.nav.sbl.dialogarena.mininnboks.consumer

import com.nhaarman.mockitokotlin2.any
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kommune
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.jupiter.api.Test

class DefaultPersonServiceTest {

    val personV3: PersonV3 = mockk()
    var personService: PersonService.Default = PersonService.Default(personV3)

    @Test
    suspend fun `henter Enhet`() {
        val enhet = "1234"
        coEvery { personV3.hentGeografiskTilknytning(any()) } returns
                HentGeografiskTilknytningResponse().withGeografiskTilknytning(Kommune().withGeografiskTilknytning(enhet))

        MatcherAssert.assertThat(personService.hentGeografiskTilknytning(any()).get(), Is.`is`(enhet))
    }

    @Test
    suspend fun `kaster Runtime Exception Om Enhet Ikke Kan hentes`() {
        try {
            personService.hentGeografiskTilknytning(any())
        } catch (e: Exception) {
            MatcherAssert.assertThat(e.message, Is.isA(RuntimeException::class.java))
        }
    }
}
