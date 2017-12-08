package no.nav.sbl.dialogarena.mininnboks;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.common.jetty.JettyStarterUtils.*;


/**
 * Login: Testfamilien Aremark: 10108000398
 */
public class StartJettyMinInnboks {

    public static void main(String[] args) {
        SystemProperties.setFrom("jetty-mininnboks.properties");
        setProperty(SubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        setProperty("henvendelse.ws.url", "https://localhost:8443/henvendelse/services/domene.Brukerdialog/Henvendelse_v2");
        setProperty("send.inn.henvendelse.ws.url", "https://localhost:8443/henvendelse/services/domene.Brukerdialog/SendInnHenvendelse_v1");
        setProperty("innsyn.henvendelse.ws.url", "https://localhost:8443/henvendelse/services/domene.Brukerdialog/InnsynHenvendelse_v1");
        setProperty("xsrf-credentials.password", "123_temp_password");
        setProperty("suspender.username", "user");
        setProperty("suspender.password", "pass");


        //Må ha https for csrf-token
        final Jetty jetty = Jetty.usingWar()
                .at("mininnboks")
                .sslPort(8586)
                .overrideWebXml()
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
