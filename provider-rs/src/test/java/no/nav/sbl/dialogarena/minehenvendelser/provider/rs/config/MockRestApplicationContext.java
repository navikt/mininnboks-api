package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config;

import no.nav.sbl.dialogarena.common.kodeverk.config.KodeverkConfig;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.HenvendelseConsumerTestContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        RestServiceConfig.class,
        KodeverkConfig.class,
        HenvendelseConsumerTestContext.class
})
public class MockRestApplicationContext {
    public static final String AKTOR_ID = "***REMOVED***";
}
