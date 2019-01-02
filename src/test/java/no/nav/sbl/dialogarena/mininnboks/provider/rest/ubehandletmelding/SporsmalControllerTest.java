package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding;

import no.nav.brukerdialog.security.context.SubjectRule;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static java.util.Collections.emptyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SporsmalControllerTest {

    @Mock
    HenvendelseService henvendelseService;

    @InjectMocks
    SporsmalController controller;

    @Rule
    public SubjectRule subjectRule = new SubjectRule(new Subject("fnr", IdentType.EksternBruker, SsoToken.oidcToken("token", emptyMap())));

    @Test
    public void kallerHenvendelseServiceMedSubjectID() throws Exception {
        when(henvendelseService.hentAlleHenvendelser(anyString())).thenReturn(new ArrayList<Henvendelse>());

        controller.ubehandledeMeldinger();

        verify(henvendelseService, times(1)).hentAlleHenvendelser(anyString());
    }
}