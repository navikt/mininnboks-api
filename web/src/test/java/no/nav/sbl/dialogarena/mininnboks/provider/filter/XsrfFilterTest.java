package no.nav.sbl.dialogarena.mininnboks.provider.filter;

import no.nav.modig.core.context.StaticSubjectHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static java.lang.System.setProperty;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.sbl.dialogarena.mininnboks.provider.sikkerhet.XsrfUtils.genererXsrfToken;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class XsrfFilterTest {

    @BeforeClass
    public static void beforeClass() {
        setProperty(SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        setProperty("xsrf-credentials.password", "123_temp_password");
    }

    @Test
    public void lagerXsrfCookieVedGET() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        new XsrfFilter().doFilter(request, response, new MockFilterChain());

        assertThat(response.getCookie("XSRF-TOKEN-MININNBOKS"), is(not(nullValue())));
    }

    @Test
    public void godkjennerGyldigHeaderVedPOST() throws IOException, ServletException {
        MockHttpSession httpSession = new MockHttpSession();

        String xsrfToken = genererXsrfToken(httpSession);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setSession(httpSession);
        request.addHeader("X-XSRF-TOKEN", xsrfToken);

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        new XsrfFilter().doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void avviserUgyldigHeaderVedPOST() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader("X-XSRF-TOKEN", "bogus");

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        new XsrfFilter().doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus(), is(SC_UNAUTHORIZED));
    }

}