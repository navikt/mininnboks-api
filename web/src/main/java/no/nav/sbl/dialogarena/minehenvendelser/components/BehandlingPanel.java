package no.nav.sbl.dialogarena.minehenvendelser.components;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.CmsContentRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.STATUS_LASTET_OPP;

public class BehandlingPanel extends Panel {

    private static final boolean MANGLENDE = false;
    private static final boolean INNSENDT = true;
    private final IModel<List<Dokumentforventning>> model;
    private Behandling behandling;
    private CmsContentRetriever innholdsTekster;

    public BehandlingPanel(String id, IModel<List<Dokumentforventning>> model, Behandling behandling, CmsContentRetriever innholdsTekster) {
        super(id, model);
        this.model = model;
        this.behandling = behandling;
        this.innholdsTekster = innholdsTekster;
        add(
                getDateText(),
                getVedleggsLabel(),
                getHeadText(),
                getTopText(),
                getInnsendteDokumenterHeader(),
                dokumenterView("innsendteDokumenter", INNSENDT),
                getManglendeDokumenterHeader(),
                dokumenterView("manglendeDokumenter", MANGLENDE), getBottomText());
    }

    private Label getInnsendteDokumenterHeader() {
        return new Label("innsendteDokumenterHeader", new ResourceModel("innsendte.dokumenter.header"));
    }

    private Label getManglendeDokumenterHeader() {
        return new Label("manglendeDokumenterHeader", new ResourceModel("manglende.dokumenter.header"));
    }

    private Label getVedleggsLabel() {
        return new Label("vedlegg", new StringResourceModel("antall.vedlegg", this, null,
                new Object[]{
                        behandling.getAntallInnsendteDokumenter()
                }));
    }

    private PropertyListView<Dokumentforventning> dokumenterView(String dokumentType, boolean statusToFilter) {
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
        return new Label("tittel", behandling.getTittel());
    }

    private Label getTopText() {
        Label l = new Label("forTekst", innholdsTekster.hentArtikkel("topp.tekst"));
        l.setEscapeModelStrings(false);
        return l;
    }

    private Label getBottomText() {
        Label l = new Label("etterTekst", innholdsTekster.hentArtikkel("slutt.tekst"));
        l.setEscapeModelStrings(false);
        return l;
    }

    private Label getDateText() {
        StringResourceModel srm =  new StringResourceModel("innsendt", this, null,behandling.getInnsendtDato().toDate());
        return new Label("innsendtDato",srm);
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
            return on(parentModel.getObject()).filter(where(STATUS_LASTET_OPP, equalTo(statusToFilter))).collect();
        }

        @Override
        public void detach() {
            parentModel.detach();
        }
    }
}
