package no.nav.sbl.dialogarena.mininnboks.consumer

import io.mockk.every
import io.mockk.mockk
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kommune
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DefaultPersonServiceTest {

    val personV3: PersonV3 = mockk();
    var personService: PersonService.Default =  PersonService.Default(personV3)

    @Test
    fun `henter Enhet`() {
        val enhet = "1234"
        every {personV3.hentGeografiskTilknytning(any())} returns HentGeografiskTilknytningResponse().withGeografiskTilknytning(Kommune().withGeografiskTilknytning(enhet))
        MatcherAssert.assertThat(personService.hentGeografiskTilknytning().get(), Is.`is`(enhet))
    }

    @Test
    fun `kaster Runtime Exception Om Enhet Ikke Kan hentes`() {
        assertThrows<RuntimeException> {personService.hentGeografiskTilknytning()}
    }
}
