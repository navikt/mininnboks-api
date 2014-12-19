package no.nav.sbl.dialogarena.mininnboks.innboks.traader;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.*;

import java.util.List;

import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.KassertInnholdUtils.getFritekstModel;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.KassertInnholdUtils.henvendelseTemagruppeKey;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.VisningUtils.henvendelseStatusTekst;
import static no.nav.sbl.dialogarena.time.Datoformat.kortMedTid;

public class TidligereMeldingerPanel extends GenericPanel<List<Henvendelse>> {

    public TidligereMeldingerPanel(String id, IModel<TraadVM> model) {
        super(id, new CompoundPropertyModel<>(model.getObject().tidligereHenvendelser()));
        setOutputMarkupPlaceholderTag(true);

        add(new ListView<Henvendelse>("tidligereMeldinger", getModel()) {
            @Override
            protected void populateItem(ListItem<Henvendelse> item) {
                Henvendelse henvendelse = item.getModelObject();
                item.add(new AvsenderBilde("avsenderBilde", item.getModel()));
                item.add(new Label("status", henvendelseStatusTekst(henvendelse)));
                item.add(new Label("sendtDato", kortMedTid(henvendelse.opprettet)));
                item.add(new Label("temagruppe", new ResourceModel(henvendelseTemagruppeKey(henvendelse.temagruppe))));
                item.add(new URLParsingMultiLineLabel("fritekst", getFritekstModel(Model.of(henvendelse))));
            }
        });
    }
}
