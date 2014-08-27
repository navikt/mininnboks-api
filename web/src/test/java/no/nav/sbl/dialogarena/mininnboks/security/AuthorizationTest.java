package no.nav.sbl.dialogarena.mininnboks.security;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.EnvironmentRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.SecurityContextRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.pdp.picketlink.PicketLinkDecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.PEPImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AuthorizationTest {

    private Authorization authorization;

    private PEPImpl pep;

    @Before
    public void setup() throws IOException {
        authorization = new Authorization();
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, "no.nav.modig.core.context.StaticSubjectHandler");
        pep = new PEPImpl(new PicketLinkDecisionPoint(new ClassPathResource("pdp/policy-config.xml").getURL()));
        pep.setRequestEnrichers(Arrays.asList(new SecurityContextRequestEnricher(), new EnvironmentRequestEnricher()));
        ReflectionTestUtils.setField(authorization, "pep", pep);
    }

    @Test
    public void skalFeileForbrukerMedDiskresjonskode6(){
        assertThat(authorization.harTilgangTilInnsending(6), is(false));
        assertThat(authorization.harTilgangTilInnsending(7), is(true));
        assertThat(authorization.harTilgangTilInnsending(null), is(true));
    }
}
