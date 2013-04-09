package no.nav.sbl.dialogarena.minehenvendelser.components;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CMSLookup;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.STATUS;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CMSLookup.lookupText;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag.hentKodeverk;

public class BehandlingPanel extends Panel {

    private static final boolean MANGLENDE = false;
    private static final boolean INNSENDT = true;
    private final IModel<List<Dokumentforventning>> model;
    private Behandling behandling;

    public BehandlingPanel(String id, IModel<List<Dokumentforventning>> model, Behandling behandling) {
        super(id, model);
        this.model = model;
        this.behandling = behandling;
        add(
                getDateText(),
                getHeadText(),
                getTopText(),
                dokumenterView("innsendteDokumenter", INNSENDT),
                dokumenterView("manglendeDokumenter", MANGLENDE),
                getBottomText()
        );
    }

    private PropertyListView dokumenterView(String dokumentType, boolean statusToFilter) {
        IModel<List<Dokumentforventning>> dokumenterLDM = new DokumentforventningModel(model, statusToFilter);
        return new PropertyListView<Dokumentforventning>(dokumentType, dokumenterLDM) {
            @Override
            protected void populateItem(ListItem<Dokumentforventning> listItem) {
                Dokumentforventning dokumentforventning = listItem.getModelObject();
                listItem.add(new Label("dokument", dokumentforventning.getTittel()));
            }
        };
    }

    private Label getHeadText() {
        return new Label("tittel", hentKodeverk(behandling.getHovedkravskjemaId()));
    }

    private Label getTopText() {
        return new Label("forTekst", lookupText("topText"));
    }


    private Label getBottomText() {
        return new Label("etterTekst", lookupText("bottomText"));
    }

    private Label getDateText() {
        return new Label("innsendtDato", behandling.getInnsendtDato());
    }

    private static class DokumentforventningModel extends LoadableDetachableModel<List<Dokumentforventning>> {

        private IModel<List<Dokumentforventning>> parentModel;
        private boolean statusToFilter;

        public DokumentforventningModel(IModel<List<Dokumentforventning>> parentModel, boolean statusToFilter) {
            this.parentModel = parentModel;
            this.statusToFilter = statusToFilter;
        }

        @Override
        protected List<Dokumentforventning> load() {
            return on(parentModel.getObject()).filter(where(STATUS, equalTo(statusToFilter))).collect();
        }

        @Override
        public void detach() {
            parentModel.detach();
        }
    }
}
