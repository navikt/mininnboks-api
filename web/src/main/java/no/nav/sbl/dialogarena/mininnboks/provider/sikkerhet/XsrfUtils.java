package no.nav.sbl.dialogarena.mininnboks.provider.sikkerhet;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.exception.AuthorizationException;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static java.lang.System.getProperty;

/**
 * Klasse som håndterer XSRF tokens
 */
public class XsrfUtils {

    public static String genererXsrfToken(String fnr) {
        return genererXsrfToken(fnr, new DateTime().toString("yyyyMMdd"));
    }

    private static String genererXsrfToken(String fnr, String dato) {
        try {
            String signKey = SubjectHandler.getSubjectHandler().getEksternSsoToken() + fnr + dato;
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hentXsrfPassord().getBytes(), "HmacSHA256");
            hmac.init(secretKey);
            return Base64.encodeBase64URLSafeString(hmac.doFinal(signKey.getBytes()));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Kunne ikke generere token: ", e);
        }
    }

    public static void sjekkXsrfToken(String givenToken, String fnr) {
        String token = genererXsrfToken(fnr);
        boolean valid = token.equals(givenToken) || genererXsrfToken(fnr, new DateTime().minusDays(1).toString("yyyyMMdd")).equals(givenToken);
        if (!valid) {
            throw new AuthorizationException("XSRF sjekk feilet: Feil token");
        }
    }

    public static Cookie xsrfCookie(String fnr, HttpSession session) {
        Cookie xsrfCookie = new Cookie("XSRF-TOKEN-MININNBOKS", genererXsrfToken(fnr));
        xsrfCookie.setPath(session.getServletContext().getContextPath());
        xsrfCookie.setMaxAge(-1);
        xsrfCookie.setSecure(true);
        return xsrfCookie;
    }

    private static String hentXsrfPassord() {
        return getProperty("xsrf-credentials.password");
    }
}
