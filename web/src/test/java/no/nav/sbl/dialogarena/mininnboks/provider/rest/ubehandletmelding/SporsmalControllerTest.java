package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SporsmalControllerTest {

    @Mock
    HenvendelseService henvendelseService;

    @InjectMocks
    SporsmalController controller;

    @Test
    public void kallerHenvendelseServiceMedSubjectID() throws Exception {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        when(henvendelseService.hentAlleHenvendelser(anyString())).thenReturn(new ArrayList<Henvendelse>());

        controller.ubehandledeMeldinger();

        verify(henvendelseService, times(1)).hentAlleHenvendelser(anyString());
    }
}