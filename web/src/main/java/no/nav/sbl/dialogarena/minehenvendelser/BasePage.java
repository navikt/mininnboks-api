package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel;
import no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel;
import no.nav.sbl.dialogarena.webkomponent.navigasjon.NavigasjonPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.wicket.model.ModelUtils.FALSE;
import static no.nav.modig.wicket.model.ModelUtils.TRUE;

public class BasePage extends WebPage {

	@Inject
	protected CmsContentRetriever cmsContentRetriever;

	public BasePage() {
		Map<String, String> footerLinks = new HashMap<>();
		footerLinks.put(FooterPanel.DIALOGARENA_FOOTER_BASEURL, System.getProperty(FooterPanel.DIALOGARENA_FOOTER_BASEURL));
		add(
                new Label("tittel", "Mine Henvendelser"),
                new InnstillingerPanel("innstillinger", TRUE, cmsContentRetriever),
                new NavigasjonPanel("navigasjon", System.getProperty("inngangsporten.link"), cmsContentRetriever),
                new FooterPanel("footer", footerLinks, TRUE, FALSE, cmsContentRetriever));
	}
	
}
