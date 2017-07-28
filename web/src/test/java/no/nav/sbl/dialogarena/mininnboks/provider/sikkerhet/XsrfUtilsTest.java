package no.nav.sbl.dialogarena.mininnboks.provider.sikkerhet;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.core.exception.AuthorizationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import static java.lang.System.setProperty;
import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.sbl.dialogarena.mininnboks.provider.sikkerhet.XsrfUtils.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class XsrfUtilsTest {

    @BeforeClass
    public static void beforeClass() {
        setProperty(SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        setProperty("xsrf-credentials.password", "123_temp_password");
    }

    @Test
    public void lagrerUuidPaaSessionVedGenerering() {
        HttpSession httpSession = new MockHttpSession();

        genererXsrfToken(httpSession);

        assertThat(httpSession.getAttribute(SESSION_UUID_ID), is(not(nullValue())));
    }

    @Test
    public void lagrerIkkeNyUuidPaaSessionHvisEnAlleredeEksisterer() {
        HttpSession httpSession = new MockHttpSession();

        genererXsrfToken(httpSession);
        String uuid = (String) httpSession.getAttribute(SESSION_UUID_ID);
        genererXsrfToken(httpSession);

        assertThat((String) httpSession.getAttribute(SESSION_UUID_ID), is(equalTo(uuid)));
    }

    @Test(expected = AuthorizationException.class)
    public void throwsHvisFeilToken() {
        HttpSession httpSession = new MockHttpSession();

        sjekkXsrfToken("bogus", httpSession);
    }

    @Test
    public void lagerCookieFraEksisterendeToken() {
        HttpSession httpSession = new MockHttpSession();

        String xsrfToken = genererXsrfToken(httpSession);

        Cookie cookie = xsrfCookie(httpSession);

        assertThat(cookie.getValue(), is(equalTo(xsrfToken)));
    }

    @Test
    public void lagerCookieMedKorrekteVerdier() {
        HttpSession httpSession = new MockHttpSession();

        Cookie cookie = xsrfCookie(httpSession);

        assertThat(cookie.getSecure(), is(true));
        assertThat(cookie.getMaxAge(), is(-1));
        assertThat(cookie.getPath(), is(equalTo(httpSession.getServletContext().getContextPath())));
    }

    @Test
    public void testXSRFPassord(){
        assertThat(System.getProperty("xsrf-credentials.password"), is("123_temp_password"));
    }

}