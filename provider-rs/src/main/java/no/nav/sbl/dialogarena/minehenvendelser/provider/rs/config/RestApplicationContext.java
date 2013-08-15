package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        RestServiceConfig.class
})
public class RestApplicationContext {
}
