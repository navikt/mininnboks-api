package no.nav.sbl.dialogarena.mininnboks.provider.rest.resources;

import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.mininnboks.consumer.EpostService;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ResourcesControllerTest {

    @Mock
    EpostService epostService;
    @Mock
    PropertyResolver propertyResolver;

    @InjectMocks
    ResourcesController controller;

    @Before
    public void setup() throws Exception {
        when(propertyResolver.getAllProperties()).thenReturn(new HashMap<String, String>() {{
            put("prop1", "val1");
            put("prop2", "val2");
            put("prop3", "val3");
        }});
        when(epostService.hentEpostadresse()).thenReturn("myMail@nav.no");
    }

    @Test
    public void henterFraPropertyResolverOgLeggerTilEgneProperties() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest(null, new MockHttpSession(new MockServletContext(null, null)), null);

        Map<String, String> resources = controller.getResources(request);

        assertThat(resources.size(), is(6));
    }

    @Test
    public void kallTilEpostServiceBlirCachet() throws Exception {
        MockHttpSession session = new MockHttpSession(new MockServletContext(null, null));

        controller.getResources(new MockHttpServletRequest(null, session, null));
        controller.getResources(new MockHttpServletRequest(null, session, null));

        verify(epostService, times(1)).hentEpostadresse();
    }
}