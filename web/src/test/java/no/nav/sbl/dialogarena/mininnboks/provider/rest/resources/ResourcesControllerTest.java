package no.nav.sbl.dialogarena.mininnboks.provider.rest.resources;

import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ResourcesControllerTest {

    @Mock
    PersonService personService;
    @Mock
    PropertyResolver propertyResolver;

    @InjectMocks
    ResourcesController controller;

    HttpSession session = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    @Before
    public void setup() throws Exception {
        when(propertyResolver.getAllProperties()).thenReturn(new HashMap<String, String>() {{
            put("prop1", "val1");
            put("prop2", "val2");
            put("prop3", "val3");
        }});
        when(session.getAttribute(ResourcesController.EPOST)).thenReturn("myMail@nav.no");
        when(request.getSession()).thenReturn(session);
        when(personService.hentEpostadresse()).thenReturn("myMail@nav.no");
    }

    @Test
    public void henterFraPropertyResolverOgLeggerTilEgneProperties() throws Exception {
        Map<String, String> resources = controller.getResources(request);
        assertThat(resources.size(), is(6));
    }

    @Test
    public void kallTilEpostServiceBlirCachet() throws Exception {
        controller.getResources(request);
        controller.getResources(request);

        verify(personService, times(0)).hentEpostadresse();
    }
}