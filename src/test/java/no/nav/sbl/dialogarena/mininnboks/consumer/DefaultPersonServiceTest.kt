package no.nav.sbl.dialogarena.mininnboks.consumer

import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kommune
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test

class DefaultPersonServiceTest {

    private val personV3: PersonV3 = mockk();
    private var personService: PersonService.Default =  PersonService.Default(personV3)

    @Test
    @Throws(Exception::class)
    fun `henter Enhet`() {
        val enhet = "1234"
        every {personV3.hentGeografiskTilknytning(any())} returns (HentGeografiskTilknytningResponse().withGeografiskTilknytning(Kommune().withGeografiskTilknytning(enhet)))
        SubjectHandler.withSubject(TestUtils.MOCK_SUBJECT) { Assert.assertThat(personService.hentGeografiskTilknytning().get(), Is.`is`(enhet)) }
    }

    @Test(expected = RuntimeException::class)
    fun `kaster Runtime Exception Om Enhet Ikke Kan hentes`() {
        personService.hentGeografiskTilknytning()
    }
}
