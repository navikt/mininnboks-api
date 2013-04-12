package no.nav.sbl.dialogarena.minehenvendelser.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ApplicationContext.class, FluentWicketTesterContext.class})
public class WicketApplicationContext {

}
