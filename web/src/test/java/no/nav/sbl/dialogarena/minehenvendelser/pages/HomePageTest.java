package no.nav.sbl.dialogarena.minehenvendelser.pages;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;

import org.apache.wicket.markup.html.list.PropertyListView;
import org.junit.Test;

public class HomePageTest extends AbstractWicketTest<WicketApplication>{

    @Test
    public void shouldRenderHomePage() {
        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("behandlingerUnderArbeid").and(ofType(PropertyListView.class)))
                .should().containComponent(withId("behandlingerFerdig").and(ofType(PropertyListView.class)));
    }

}
