package no.nav.sbl.dialogarena.minehenvendelser.test;

import no.nav.sbl.dialogarena.minehenvendelser.config.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ApplicationContext.class, FluentWicketTesterContext.class})
public class WicketApplicationContext {

}
