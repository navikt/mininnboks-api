package no.nav.sbl.dialogarena.minehenvendelser.components;

import static java.lang.String.format;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Dokumentbehandlingstatus.DOKUMENT_ETTERSENDING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.STATUS_LASTET_OPP;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.util.ListModel;

/**
 * Hovedpanel som inneholder samtlige dokumentforventninger knyttet til en behandling.
 */
public class BehandlingPanel extends GenericPanel<List<Dokumentforventning>> {

    private static final boolean MANGLENDE = false;
    private static final boolean INNSENDT = true;
    private Behandling behandling;
    private CmsContentRetriever innholdsTekster;
    private Kodeverk kodeverkOppslag;

    public BehandlingPanel(Behandling behandling, CmsContentRetriever innholdsTekster, Kodeverk kodeverkOppslag, Locale locale) {
        super("behandling", new ListModel<>(behandling.getRelevanteDokumenter()));
        this.behandling = behandling;
        this.innholdsTekster = innholdsTekster;
        this.kodeverkOppslag = kodeverkOppslag;
        add(
                new Label("visKvittering", innholdsTekster.hentTekst("behandling.vis.kvittering")),
                new Label("skjulKvittering", innholdsTekster.hentTekst("behandling.skjul.kvittering")),
                getDateText(locale),
                getVedleggsLabel(),
                getHeadText(),
                getTopText(),
                getInnsendteDokumenterHeader(),
                dokumenterView("innsendteDokumenter", INNSENDT),
                getManglendeDokumenterHeader(),
                dokumenterView("manglendeDokumenter", MANGLENDE), getBottomText());
    }

    private Label getInnsendteDokumenterHeader() {
        Label innsendteDokumenterHeader = new Label("innsendteDokumenterHeader",
                innholdsTekster.hentTekst("behandling.innsendte.dokumenter.header"));
        if (behandling.getAntallInnsendteDokumenter() == 0) {
            innsendteDokumenterHeader.setVisible(false);
        }
        return innsendteDokumenterHeader;
    }

    private Label getManglendeDokumenterHeader() {
        Label manglendeDokumenterHeader = new Label("manglendeDokumenterHeader",
                innholdsTekster.hentTekst("behandling.manglende.dokumenter.header"));
        if (behandling.getAntallManglendeDokumenter() == 0) {
            manglendeDokumenterHeader.setVisible(false);
        }
        return manglendeDokumenterHeader;
    }

    private Label getVedleggsLabel() {
        if (behandling.getDokumentbehandlingstatus() == DOKUMENT_ETTERSENDING) {
            return createFormattedLabel("vedlegg",
                    innholdsTekster.hentTekst("behandling.antall.vedlegg"),
                    behandling.getAntallInnsendteDokumenterUnntattHovedDokument(),
                    behandling.getAntallDokumenterUnntattHovedDokument());
        } else {
            return createFormattedLabel("vedlegg",
                    innholdsTekster.hentTekst("behandling.antall.vedlegg"),
                    behandling.getAntallInnsendteDokumenter(),
                    behandling.getAntallDokumenter());
        }
    }

    private PropertyListView<Dokumentforventning> dokumenterView(String dokumentType, boolean statusToFilter) {
        IModel<List<Dokumentforventning>> dokumenterLDM = new DokumentforventningModel(getModel(), statusToFilter);
        return new PropertyListView<Dokumentforventning>(dokumentType, dokumenterLDM) {
            @Override
            protected void populateItem(ListItem<Dokumentforventning> listItem) {
                Dokumentforventning dokumentforventning = listItem.getModelObject();
                if (kodeverkOppslag.isEgendefinert(dokumentforventning.getKodeverkId())) {
                    listItem.add(new Label("dokument", kodeverkOppslag.getTittel(dokumentforventning.getKodeverkId()) + dokumentforventning.getFriTekst()));
                } else {
                    listItem.add(new Label("dokument", kodeverkOppslag.getTittel(dokumentforventning.getKodeverkId())));
                }
            }
        };
    }

    private Label getHeadText() {
        if (behandling.getDokumentbehandlingstatus() == DOKUMENT_ETTERSENDING) {
            return createFormattedLabel("tittel",
                    innholdsTekster.hentTekst("behandling.ettersending.tekst"),
                    kodeverkOppslag.getTittel(behandling.getKodeverkId()));
        }
        return new Label("tittel", kodeverkOppslag.getTittel(behandling.getKodeverkId()));
    }

    private Label createFormattedLabel(String wicketId, String unformattedText, Object... args) {
        return new Label(wicketId, format(unformattedText, args));
    }

    private Label getTopText() {
        return new Label("forTekst", innholdsTekster.hentTekst("behandling.topp.tekst"));
    }

    private Label getBottomText() {
        return (Label) new Label("etterTekst", innholdsTekster.hentTekst("behandling.slutt.tekst")).setEscapeModelStrings(false);
    }

    private Label getDateText(Locale locale) {
        String formattedDate = new SimpleDateFormat("d. MMMM YYYY, HH:mm", locale).format(behandling.getInnsendtDato().toDate());
        return new Label("innsendtDato", formattedDate);
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
