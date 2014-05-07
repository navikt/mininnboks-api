package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.innboks.TraadVM.getNyesteHenvendelse;

public class NyesteMeldingPanel extends Panel {

    public NyesteMeldingPanel(String id, IModel<TraadVM> model) {
        super(id, model);

        List<Henvendelse> henvendelser = model.getObject().henvendelser;
        Henvendelse nyesteHenvendelse = getNyesteHenvendelse(henvendelser);

        add(new Label("sendt-dato", nyesteHenvendelse.opprettet));
        add(new Label("tema", nyesteHenvendelse.tema.name()));
        add(new Label("traadlengde", henvendelser.size()));
        add(new URLParsingMultiLineLabel("fritekst", nyesteHenvendelse.fritekst));
    }
}
