package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.minehenvendelser.config.ApplicationContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.SystemProperties.load;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContext.class)
public class ApplicationContextTest {

    @BeforeClass
    public static void beforeClass() throws IOException {
        load("/environment-test.properties");
        TestCertificates.setupKeyAndTrustStore();
    }

    @Test
    public void shouldSetupAppContext() {}

}
