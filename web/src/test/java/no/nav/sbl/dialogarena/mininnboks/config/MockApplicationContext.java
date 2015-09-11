package no.nav.sbl.dialogarena.mininnboks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MinInnboksApplicationContext.class, PersonServiceMockContext.class, HenvendelseMockContext.class})
public class MockApplicationContext {
}
