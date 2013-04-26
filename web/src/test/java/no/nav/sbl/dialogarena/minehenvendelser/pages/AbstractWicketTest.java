package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;

import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

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

    protected abstract void setup();

}
