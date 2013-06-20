package no.nav.sbl.dialogarena.minehenvendelser.components;

import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.ApplicationConstants.DATO_FORMAT;
import static no.nav.sbl.dialogarena.minehenvendelser.ApplicationConstants.DEFAULT_LOCALE;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus.UNDER_ARBEID;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Dokumentbehandlingstatus.DOKUMENT_ETTERSENDING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.STATUS;

public class BehandlingerUnderArbeidListView extends BehandlingerListView {

    @Inject
    private AktoerIdService aktoerIdService;

    public BehandlingerUnderArbeidListView(String id, List<? extends Behandling> behandlinger) {
        super(id, filterAndSort(behandlinger));
    }

    private static List<? extends Behandling> filterAndSort(List<? extends Behandling> behandlinger) {
        return on(behandlinger).filter(where(STATUS, equalTo(UNDER_ARBEID))).collect(new Comparator<Behandling>() {
            @Override
            public int compare(Behandling arg0, Behandling arg1) {
                return arg1.getSistEndret().compareTo(arg0.getSistEndret());
            }
        });
    }

    @Override
    protected void populateItem(ListItem<Behandling> item) {
        String dokumentInnsendingUrl = getProperty("dokumentinnsending.link.url") + item.getModelObject().getBehandlingsId();
        String formattedDate = new SimpleDateFormat(DATO_FORMAT, DEFAULT_LOCALE).format(item.getModelObject().getSistEndret().toDate());
        item.add(
                getTittel(item.getModelObject()),
                createFormattedLabel("sistEndret", innholdstekster.hentTekst("behandling.siste.endret"), formattedDate),
                new ExternalLink("fortsettLink", dokumentInnsendingUrl, innholdstekster.hentTekst("behandling.fortsett.innsending.link.tekst")));
    }

    private Label getTittel(Behandling item) {
        if (item.getDokumentbehandlingstatus() == DOKUMENT_ETTERSENDING) {
            return createFormattedLabel("tittel", innholdstekster.hentTekst("behandling.ettersending.tekst"), kodeverk.getTittel(item.getKodeverkId()));
        }
        return new Label("tittel", kodeverk.getTittel(item.getKodeverkId()));
    }

    private Label createFormattedLabel(String wicketId, String unformattedText, Object... args) {
        return new Label(wicketId, format(unformattedText, args));
    }

}
