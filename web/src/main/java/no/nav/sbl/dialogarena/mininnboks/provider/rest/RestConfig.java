package no.nav.sbl.dialogarena.mininnboks.provider.rest;

import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel.SporsmalController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig extends ResourceConfig {

    public RestConfig() {
        super(DateTimeObjectMapperProvider.class, SporsmalController.class, HenvendelseController.class);
    }
}
