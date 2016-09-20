package no.nav.sbl.dialogarena.mininnboks.provider.rest;

import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.logger.JSLoggerController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.ResourcesController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.SporsmalController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig extends ResourceConfig {

    public RestConfig() {
        super(DateTimeObjectMapperProvider.class, SporsmalController.class, HenvendelseController.class, ResourcesController.class, JSLoggerController.class);
    }
}
