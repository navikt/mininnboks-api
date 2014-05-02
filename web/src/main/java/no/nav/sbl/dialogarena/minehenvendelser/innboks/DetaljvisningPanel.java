package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.innboks.sporsmalogsvar.TraaddetaljerPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.either;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelsetype.SVAR;

public class DetaljvisningPanel extends Panel {

    public DetaljvisningPanel(String id, InnboksVM innboksVM) {
		super(id);
		setOutputMarkupId(true);
        TraaddetaljerPanel traaddetaljerPanel = new TraaddetaljerPanel("traad", new CompoundPropertyModel<>(innboksVM));
        traaddetaljerPanel.add(visibleIf(either(innboksVM.valgtHenvendelseAvType(SPORSMAL)).or(innboksVM.valgtHenvendelseAvType(SVAR))));
		add(traaddetaljerPanel);
	}

    @RunOnEvents(Innboks.VALGT_HENVENDELSE)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }

}
