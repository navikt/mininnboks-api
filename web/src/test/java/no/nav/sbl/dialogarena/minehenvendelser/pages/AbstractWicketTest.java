package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.utils.UTF8Control;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.WicketApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractWicketTest {

    protected ApplicationContextMock applicationContext;
    protected FluentWicketTester<WicketApplication> wicketTester;

    @Before
    public void before() {
        applicationContext = new ApplicationContextMock() {
            @Override
            public long getStartupDate() {
                return System.currentTimeMillis();
            }
        };

        WicketApplication wicketApplication = new WicketApplication() {
            @Override
            protected void setSpringComponentInjector() {
                getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
            }

            @Override
            public ApplicationContext getApplicationContext() {
                return applicationContext;
            }
        };
        wicketTester = new FluentWicketTester<WicketApplication>(wicketApplication);
        setup();
    }

    protected <T> T mock(Class<T> clazz) {
        T mock = Mockito.mock(clazz);
        applicationContext.putBean(mock);
        return mock;
    }

    protected <T> T mock(String beanName, Class<T> clazz) {
        T mock = Mockito.mock(clazz);
        applicationContext.putBean(beanName, mock);
        return mock;
    }

    protected String mock(String beanName, String value) {
        applicationContext.putBean(beanName, value);
        return value;
    }

    protected abstract void setup();

    protected void setupFakeCms() {
        ValueRetriever tekstValueRetriever = new ValueRetriever() {
            private ResourceBundle appBundle = ResourceBundle.getBundle("content/innholdstekster", new Locale("nb"), new UTF8Control());
            private ResourceBundle webkomponenterBundle = ResourceBundle.getBundle("content/sbl-webkomponenter", new Locale("nb"), new UTF8Control());

            @Override
            public String getValueOf(String key, String language) {
                return appBundle.containsKey(key) ? appBundle.getString(key) : webkomponenterBundle.getString(key);
            }
        };
        CmsContentRetriever innholdstekster = new CmsContentRetriever();
        innholdstekster.setTeksterRetriever(tekstValueRetriever);
        innholdstekster.setDefaultLocale("no");
        applicationContext.putBean(innholdstekster);
    }

}
