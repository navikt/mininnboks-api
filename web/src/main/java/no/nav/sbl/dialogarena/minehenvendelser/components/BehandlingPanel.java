package no.nav.sbl.dialogarena.minehenvendelser.components;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;
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
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Dokumentbehandlingstatus.DOKUMENT_ETTERSENDING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.STATUS_LASTET_OPP;

/**
 * Hovedpanel som inneholder samtlige dokumentforventninger knyttet til en behandling.
 */
public class BehandlingPanel extends Panel {

    private static final boolean MANGLENDE = false;
    private static final boolean INNSENDT = true;
    private final IModel<List<Dokumentforventning>> model;
    private Behandling behandling;
    private CmsContentRetriever innholdsTekster;
    private KodeverkService kodeverkOppslag;

    public BehandlingPanel(String id, IModel<List<Dokumentforventning>> model, Behandling behandling, CmsContentRetriever innholdsTekster, KodeverkService kodeverkOppslag) {
        super(id, model);
        this.model = model;
        this.behandling = behandling;
        this.innholdsTekster = innholdsTekster;
        this.kodeverkOppslag = kodeverkOppslag;
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
        Label innsendteDokumenterHeader = new Label("innsendteDokumenterHeader", new ResourceModel("innsendte.dokumenter.header"));
        if (behandling.getAntallInnsendteDokumenter() == 0) {
            innsendteDokumenterHeader.setVisible(false);
        }
        return innsendteDokumenterHeader;
    }

    private Label getManglendeDokumenterHeader() {
        Label manglendeDokumenterHeader = new Label("manglendeDokumenterHeader", new ResourceModel("manglende.dokumenter.header"));
        if (behandling.getAntallManglendeDokumenter() == 0) {
            manglendeDokumenterHeader.setVisible(false);
        }
        return manglendeDokumenterHeader;
    }

    private Label getVedleggsLabel() {
        if (behandling.getDokumentbehandlingstatus() == DOKUMENT_ETTERSENDING) {
            return new Label("vedlegg", new StringResourceModel("antall.vedlegg", this, null, behandling.getAntallInnsendteDokumenterUnntattHovedDokument(), behandling.getAntallDokumenterUnntattHovedDokument()));
        } else {
            return new Label("vedlegg", new StringResourceModel("antall.vedlegg", this, null, behandling.getAntallInnsendteDokumenter(), behandling.getAntallDokumenter()));
        }
    }

    private PropertyListView<Dokumentforventning> dokumenterView(String dokumentType, boolean statusToFilter) {
        IModel<List<Dokumentforventning>> dokumenterLDM = new DokumentforventningModel(model, statusToFilter);
        return new PropertyListView<Dokumentforventning>(dokumentType, dokumenterLDM) {
            @Override
            protected void populateItem(ListItem<Dokumentforventning> listItem) {
                Dokumentforventning dokumentforventning = listItem.getModelObject();
                if (kodeverkOppslag.isEgendefKode(dokumentforventning.getTittel())) {
                    listItem.add(new Label("dokument", kodeverkOppslag.hentKodeverk(dokumentforventning.getTittel()) + dokumentforventning.getFriTekst()));
                } else {
                    listItem.add(new Label("dokument", kodeverkOppslag.hentKodeverk(dokumentforventning.getTittel())));
                }
            }
        };
    }

    private Label getHeadText() {
        if (behandling.getDokumentbehandlingstatus() == DOKUMENT_ETTERSENDING) {
            return new Label("tittel", new StringResourceModel("ettersending.tekst", this, null, null, kodeverkOppslag.hentKodeverk(behandling.getTittel())));
        }
        return new Label("tittel", kodeverkOppslag.hentKodeverk(behandling.getTittel()));
    }

    private Label getTopText() {
        Label topTextLabel = new Label("forTekst", innholdsTekster.hentArtikkel("topp.tekst"));
        topTextLabel.setEscapeModelStrings(false);
        return topTextLabel;
    }

    private Label getBottomText() {
        Label bottomTextLabel = new Label("etterTekst", innholdsTekster.hentArtikkel("slutt.tekst"));
        bottomTextLabel.setEscapeModelStrings(false);
        return bottomTextLabel;
    }

    private Label getDateText() {
        StringResourceModel stringResourceModel = new StringResourceModel("innsendt", this, null, behandling.getInnsendtDato().toDate());
        return new Label("innsendtDato", stringResourceModel);
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
