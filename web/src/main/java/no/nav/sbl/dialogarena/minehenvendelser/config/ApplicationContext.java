package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerContext;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.consumer.context.WsMockTestContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({WebContext.class, PropertyPlaceholderConfiguration.class})
public class ApplicationContext {

    @Configuration
    @Profile("test")
    @Import({ConsumerContext.class, WsMockTestContext.class})
    public static class ServiceTestContext {}

    @Configuration
    @Profile("default")
    @Import(ConsumerContext.class)
    public static class ServiceRealContext {}

    @Configuration
    @Profile("stub")
    @Import({ConsumerContext.class})
    public static class ServiceStubContext {}

}
