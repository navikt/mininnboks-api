package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.sporsmalogsvar.TraaddetaljerPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.either;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Meldingstype.SVAR;

public class DetaljvisningPanel extends Panel {

    public DetaljvisningPanel(String id, InnboksModell innboksModell) {
		super(id);
		setOutputMarkupId(true);
        TraaddetaljerPanel traaddetaljerPanel = new TraaddetaljerPanel("traad");
        traaddetaljerPanel.add(visibleIf(either(innboksModell.valgtMeldingAvType(SPORSMAL)).or(innboksModell.valgtMeldingAvType(SVAR))));
		add(traaddetaljerPanel);
	}

    @RunOnEvents(Innboks.VALGT_MELDING)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }

}
