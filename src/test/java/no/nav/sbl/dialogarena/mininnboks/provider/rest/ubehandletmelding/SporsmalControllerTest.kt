package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.brukerdialog.security.context.SubjectRule
import no.nav.brukerdialog.security.domain.IdentType
import no.nav.common.auth.SsoToken
import no.nav.common.auth.Subject
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.*
@Ignore
@RunWith(MockitoJUnitRunner::class)
class SporsmalControllerTest {
    @Mock
    var henvendelseService: HenvendelseService? = null

    @InjectMocks
    var controller: SporsmalController? = null

    @Rule
    var subjectRule = SubjectRule(Subject("fnr", IdentType.EksternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>())))

    @Test
    @Throws(Exception::class)
    fun kallerHenvendelseServiceMedSubjectID() {
        Mockito.`when`(henvendelseService!!.hentAlleHenvendelser(ArgumentMatchers.anyString())).thenReturn(ArrayList())
        controller!!.ubehandledeMeldinger()
        Mockito.verify(henvendelseService, Mockito.times(1))?.hentAlleHenvendelser(ArgumentMatchers.anyString())
    }
}
