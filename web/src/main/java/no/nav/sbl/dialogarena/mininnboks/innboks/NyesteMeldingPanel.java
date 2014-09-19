package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.List;

import static no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM.getNyesteHenvendelse;
import static no.nav.sbl.dialogarena.time.Datoformat.kortMedTid;

public class NyesteMeldingPanel extends Panel {

    public NyesteMeldingPanel(String id, IModel<TraadVM> model) {
        super(id, model);

        List<Henvendelse> henvendelser = model.getObject().henvendelser;
        Henvendelse nyesteHenvendelse = getNyesteHenvendelse(henvendelser);

        add(new AvsenderBilde("avsenderBilde", nyesteHenvendelse));
        add(new Label("sendtDato", kortMedTid(nyesteHenvendelse.opprettet)));
        add(new Label("temagruppe",
                new StringResourceModel("melding.temagruppe", null, new Object[]{
                        new ResourceModel(nyesteHenvendelse.type.name()),
                        new ResourceModel(nyesteHenvendelse.temagruppe.name())
                })
        ));
        add(new Label("traadlengde", henvendelser.size()));
        add(new URLParsingMultiLineLabel("fritekst", nyesteHenvendelse.fritekst));
    }
}
