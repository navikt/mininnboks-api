package no.nav.sbl.dialogarena.minehenvendelser.provider.rs;

import no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InnsendingerProviderTest {

    private Dispatcher dispatcher;

    @Mock
    private InnsendingerService innsendingerService;

    @InjectMocks
    private InnsendingerProvider innsendingerProvider = new InnsendingerProvider();

    @Before
    public void setUp() {
        dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(innsendingerProvider);
    }

    @Test
    public void skalHentePaabegynteInnsendinger() throws Exception {
        MockHttpRequest request = MockHttpRequest.get("/innsendinger/paabegynte");
        MockHttpResponse response = new MockHttpResponse();
        when(innsendingerService.getPaabegynte()).thenReturn(getPaabegynteMockData());

        dispatcher.invoke(request, response);

        assertThat(response.getContentAsString(), is("[{\"innsending\":{\"innsendingUrl\":{\"tekst\":\"lenketittel\",\"url\":\"http:\\/\\/some.url.com\\/\"},\"status\":\"IKKE_SENDT_TIL_NAV\",\"tittel\":\"en tittel\"}}]"));
    }

    private List<Innsending> getPaabegynteMockData() {
        List<Innsending> paabegynte = new ArrayList<>();
        Innsending innsending1 = new Innsending();
        innsending1.setTittel("en tittel");
        innsending1.setDato(null);
        innsending1.setInnsendingUrl(new Innsending.InnsendingUrl("lenketittel", "http://some.url.com/"));
        innsending1.setStatus(Innsending.InnsendingStatus.IKKE_SENDT_TIL_NAV);
        paabegynte.add(innsending1);

        return paabegynte;
    }
}
