package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Locale;

@Configuration
@Import({ApplicationContext.class, FluentWicketTesterContext.class})
public class WicketApplicationContext {

}
