package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import static no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM.getTidligereHenvendelser;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.KassertInnholdUtils.getFritekstModel;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.KassertInnholdUtils.henvendelseTemagruppeKey;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.VisningUtils.henvendelseStatusTekst;
import static no.nav.sbl.dialogarena.time.Datoformat.kortMedTid;

public class TidligereMeldingerPanel extends Panel {

    public TidligereMeldingerPanel(String id, IModel<TraadVM> model) {
        super(id, model);

        add(new ListView<Henvendelse>("tidligereMeldinger", getTidligereHenvendelser(model.getObject().henvendelser)) {
            @Override
            protected void populateItem(ListItem<Henvendelse> item) {
                Henvendelse henvendelse = item.getModelObject();
                item.add(new AvsenderBilde("avsenderBilde", henvendelse));
                item.add(new Label("status", henvendelseStatusTekst(henvendelse)));
                item.add(new Label("sendtDato", kortMedTid(henvendelse.opprettet)));
                item.add(new Label("temagruppe", new ResourceModel(henvendelseTemagruppeKey(henvendelse))));
                item.add(new URLParsingMultiLineLabel("fritekst", getFritekstModel(henvendelse)));
            }
        });
    }
}
