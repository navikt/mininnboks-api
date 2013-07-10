package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.cache.CacheConfig;
import no.nav.modig.core.context.ModigSecurityConstants;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.common.kodeverk.config.KodeverkConfig;
import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.WicketApplication;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import javax.inject.Inject;
import java.util.Locale;

@Configuration
@Import({
        ConsumerTestContext.class,
        CacheConfig.class,
        KodeverkConfig.class,
        WebContext.class
})
@PropertySource("classpath:environment-test.properties")
public class FitNesseApplicationContext {

    @Inject
    private WicketApplication application;

    static {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
        System.setProperty("no.nav.modig.security.sts.url", "http://localhost:9080/SecurityTokenServiceProvider/");
        System.setProperty(ModigSecurityConstants.SYSTEMUSER_USERNAME, "BD03");
        System.setProperty(ModigSecurityConstants.SYSTEMUSER_PASSWORD, "CHANGEME");
    }

    @Bean
    public FluentWicketTester<WicketApplication> wicketTester() {
        FluentWicketTester<WicketApplication> wicketTester = new FluentWicketTester<>(application);
        wicketTester.tester.getSession().setLocale(new Locale("nb"));
        return wicketTester;
    }

    @Bean
    public FoedselsnummerService foedselsnummerService() {
        return new FoedselsnummerService();
    }

}
