package no.nav.sbl.dialogarena.mininnboks.consumer

import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kommune
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DefaultPersonServiceTest {
    @Mock
    private val personV3: PersonV3? = null
    private var personService: PersonService.Default? = null

    @Before
    fun setUp() {
        personService = PersonService.Default(personV3!!)
    }

    @Test
    @Throws(Exception::class)
    fun henterEnhet() {
        val enhet = "1234"
        Mockito.`when`(personV3!!.hentGeografiskTilknytning(ArgumentMatchers.any(HentGeografiskTilknytningRequest::class.java)))
                .thenReturn(HentGeografiskTilknytningResponse().withGeografiskTilknytning(Kommune().withGeografiskTilknytning(enhet)))
        SubjectHandler.withSubject(TestUtils.MOCK_SUBJECT) { Assert.assertThat(personService!!.hentGeografiskTilknytning().get(), Is.`is`(enhet)) }
    }

    @Test(expected = RuntimeException::class)
    fun kasterRuntimeExceptionOmEnhetIkkeKanhentes() {
        personService!!.hentGeografiskTilknytning()
    }
}
