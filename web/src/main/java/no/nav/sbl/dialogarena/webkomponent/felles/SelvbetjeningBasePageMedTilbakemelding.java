package no.nav.sbl.dialogarena.webkomponent.felles;

import static no.nav.modig.frontend.FrontendModules.EKSTERNFLATE;
import static no.nav.modig.frontend.FrontendModules.UNDERSCORE;
import static no.nav.modig.frontend.MetaTag.CHARSET_UTF8;
import static no.nav.modig.frontend.MetaTag.VIEWPORT_SCALE_1;
import static no.nav.modig.frontend.MetaTag.XUA_IE_EDGE;
import static no.nav.modig.wicket.model.ModelUtils.FALSE;
import static no.nav.modig.wicket.model.ModelUtils.TRUE;
import static no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel.INNSTILLINGER_JS;
import static no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel.INNSTILLINGER_LESS;
import static no.nav.sbl.dialogarena.webkomponent.tilbakemelding.web.TilbakemeldingContainer.TILBAKEMELDING_JS;
import static no.nav.sbl.dialogarena.webkomponent.tilbakemelding.web.TilbakemeldingContainer.TILBAKEMELDING_LESS;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel;
import no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel;
import no.nav.sbl.dialogarena.webkomponent.navigasjon.NavigasjonPanel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class SelvbetjeningBasePageMedTilbakemelding extends WebPage {
	
    @Inject
    protected CmsContentRetriever cmsContentRetriever;

    @Inject
    @Named("navigasjonslink")
    private String navigasjonsLink;

    @Inject
    @Named("footerLinks")
    private Map<String, String> footerLinks;

    public SelvbetjeningBasePageMedTilbakemelding() {
        add(
                new Label("tittel", cmsContentRetriever.hentTekst("hoved.tittel")),
                new InnstillingerPanel("innstillinger", TRUE, cmsContentRetriever),
                new NavigasjonPanel("navigasjon", navigasjonsLink),
                new FooterPanel("footer", footerLinks, TRUE, FALSE, cmsContentRetriever)
        );
    }
    

	public static FrontendConfigurator defaultFrontentConfigurator() {
		return new FrontendConfigurator()
                .withModules(EKSTERNFLATE, UNDERSCORE)
                .addMetas(CHARSET_UTF8, VIEWPORT_SCALE_1, XUA_IE_EDGE)
                .addLess(TILBAKEMELDING_LESS, INNSTILLINGER_LESS)
                .addScripts(TILBAKEMELDING_JS, INNSTILLINGER_JS);
	}

}
