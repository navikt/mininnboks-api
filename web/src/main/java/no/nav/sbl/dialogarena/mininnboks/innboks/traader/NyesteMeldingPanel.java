package no.nav.sbl.dialogarena.mininnboks.innboks.traader;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.innboks.AvsenderBilde;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.*;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.KassertInnholdUtils.getFritekstModel;

public class NyesteMeldingPanel extends GenericPanel<Henvendelse> {

    public NyesteMeldingPanel(String id, IModel<TraadVM> model) {
        super(id, new CompoundPropertyModel<>(model.getObject().nyesteHenvendelse()));
        setOutputMarkupId(true);

        add(new AvsenderBilde("avsenderBilde", getModel()));
        add(new Label("statusTekst"));
        add(new Label("sendtDato"));
        add(new Label("temagruppe", new StringResourceModel("${temagruppeKey}", getModel())));
        add(new Label("traadlengde", model.getObject().getTraadlengde()).add(visibleIf(model.getObject().visTraadlengde())));
        add(new URLParsingMultiLineLabel("fritekst", getFritekstModel(getModel())));
    }

}
