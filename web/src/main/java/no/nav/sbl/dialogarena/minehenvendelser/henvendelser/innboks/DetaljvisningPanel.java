package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.either;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Meldingstype.SVAR;

public class DetaljvisningPanel extends Panel {

    public DetaljvisningPanel(String id, InnboksModell innboksModell) {
		super(id);
		setOutputMarkupId(true);
        MeldingstraadPanel meldingstraadPanel = new MeldingstraadPanel("traad");
        meldingstraadPanel.add(visibleIf(either(innboksModell.valgtMeldingAvType(SPORSMAL)).or(innboksModell.valgtMeldingAvType(SVAR))));
        add(new Label("ingen-valgt", new StringResourceModel("ingen-melding-valgt", this, null)).add(visibleIf(innboksModell.ingenMeldingValgt())));
		add(meldingstraadPanel);
	}

    @SuppressWarnings("unused")
    @RunOnEvents(Innboks.VALGT_MELDING)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }

}
