package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SVAR;
import static no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM.getTidligereHenvendelser;
import static no.nav.sbl.dialogarena.time.Datoformat.kortMedTid;

public class TidligereMeldingerPanel extends Panel {

    public TidligereMeldingerPanel(String id, IModel<TraadVM> model) {
        super(id, model);

        add(new ListView<Henvendelse>("tidligere-meldinger", getTidligereHenvendelser(model.getObject().henvendelser)) {
            @Override
            protected void populateItem(ListItem<Henvendelse> item) {
                item.add(new WebMarkupContainer("avsender-bilde")
                        .add(hasCssClassIf("avsender-nav", Model.of(item.getModelObject().type == SVAR)))
                        .add(hasCssClassIf("avsender-bruker", Model.of(item.getModelObject().type == SPORSMAL))));
                item.add(new Label("sendt-dato", kortMedTid(item.getModelObject().opprettet)));
                item.add(new Label("tema", new StringResourceModel(item.getModelObject().tema.name(), TidligereMeldingerPanel.this, null)));
                item.add(new URLParsingMultiLineLabel("fritekst", item.getModelObject().fritekst));
            }
        });
    }
}
