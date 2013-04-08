package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.pages.HomePage;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WicketApplicationContext.class)
public class HomePageTest {

    @Inject
    private FluentWicketTester<WicketApplication> wicketTester;

    @Test
    public void shouldRenderHomePage() {
        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("behandlingerUnderArbeid").and(ofType(PropertyListView.class)))
                .should().containComponent(withId("behandlingerFerdig").and(ofType(PropertyListView.class)));
    }

}
