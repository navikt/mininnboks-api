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
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.mininnboks.provider.sikkerhet.XsrfUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class XsrfUtilsTest {

    @BeforeClass
    public static void beforeClass() {
        setProperty(SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        setProperty("xsrf-credentials.password", "123_temp_password");
    }

    @Test(expected = AuthorizationException.class)
    public void throwsHvisFeilToken() {
        String fnr = getSubjectHandler().getUid();

        sjekkXsrfToken("bogus", fnr);
    }

    @Test
    public void lagerCookieFraEksisterendeToken() {
        HttpSession httpSession = new MockHttpSession();
        String fnr = getSubjectHandler().getUid();

        String xsrfToken = genererXsrfToken(fnr);

        Cookie cookie = xsrfCookie(fnr, httpSession);

        assertThat(cookie.getValue(), is(equalTo(xsrfToken)));
    }

    @Test
    public void lagerCookieMedKorrekteVerdier() {
        HttpSession httpSession = new MockHttpSession();
        String fnr = getSubjectHandler().getUid();

        Cookie cookie = xsrfCookie(fnr, httpSession);

        assertThat(cookie.getSecure(), is(true));
        assertThat(cookie.getMaxAge(), is(-1));
        assertThat(cookie.getPath(), is(equalTo(httpSession.getServletContext().getContextPath())));
    }

    @Test
    public void testXSRFPassord(){
        assertThat(System.getProperty("xsrf-credentials.password"), is("123_temp_password"));
    }

}