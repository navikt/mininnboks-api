package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.InnsendingStatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.InnsendingStatus.IKKE_SENDT_TIL_NAV;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.InnsendingStatus.MOTTATT;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.InnsendingStatus.UNDER_BEHANDLING;

public final class Innsending {

    public enum InnsendingStatus { IKKE_SENDT_TIL_NAV, MOTTATT, UNDER_BEHANDLING, FERDIG }

    private String tittel;
    private InnsendingStatus status;
    private DateTime dato;
    private InnsendingUrl innsendingUrl;

    private Innsending() { }

    public String getTittel() {
        return tittel;
    }

    public InnsendingStatus getStatus() {
        return status;
    }

    public DateTime getDato() {
        return dato;
    }

    public InnsendingUrl getInnsendingUrl() {
        return innsendingUrl;
    }

    public static InnsendingStatus convertToInnsendingStatus(SoeknadsStatus soeknadsStatus) {
        switch (soeknadsStatus) {

            case FERDIG:
                return FERDIG;
            case UNDER_ARBEID:
                return UNDER_BEHANDLING;
            case MOTTATT:
                return  MOTTATT;
            default:
                return IKKE_SENDT_TIL_NAV;
        }
    }

    public static InnsendingStatus convertToInnsendingStatus(Henvendelsesbehandling.Behandlingsstatus behandlingsstatus) {
        switch (behandlingsstatus) {

            case FERDIG:
                return MOTTATT;
            case AVBRUTT_AV_BRUKER:
            case IKKE_SPESIFISERT:
            case UNDER_ARBEID:
            default:
                return IKKE_SENDT_TIL_NAV;

        }
    }

    public static Transformer<Soeknad, Innsending> soeknadTransformer(final CmsContentRetriever innholdstekster) {

        return new Transformer<Soeknad, Innsending>() {

            @Override
            public Innsending transform(Soeknad soeknad) {
                Innsending innsending = new Innsending();
                innsending.tittel = soeknad.getTema();
                innsending.status = convertToInnsendingStatus(soeknad.getSoeknadsStatus());
                innsending.dato = soeknad.getMottatt();
                innsending.innsendingUrl = new InnsendingUrl(
                        innholdstekster.hentTekst("soeknad.detaljer.link.tekst"),
                        getProperty("soeknad.detaljer.link.url") + soeknad.getBehandlingsId());
                return innsending;
            }
        };
    }

    public static Transformer<Henvendelsesbehandling, Innsending> behandlingTransformer(final CmsContentRetriever innholdstekster, final Kodeverk kodeverk) {
        return new Transformer<Henvendelsesbehandling, Innsending>() {

            @Override
            public Innsending transform(Henvendelsesbehandling henvendelsesbehandling) {
                Innsending innsending = new Innsending();
                innsending.tittel = kodeverk.getTittel(henvendelsesbehandling.fetchHoveddokument().getKodeverkId());
                innsending.status = convertToInnsendingStatus(henvendelsesbehandling.getBehandlingsstatus());
                innsending.dato = henvendelsesbehandling.getSistEndret();
                innsending.innsendingUrl = new InnsendingUrl(
                        innholdstekster.hentTekst("behandling.fortsett.innsending.link.tekst"),
                        getProperty("dokumentinnsending.link.url") + henvendelsesbehandling.getBehandlingsId());
                return innsending;
            }
        };
    }

    public static class InnsendingUrl {
        private String tekst;
        private String url;

        public InnsendingUrl(String tekst, String url) {
            this.tekst = tekst;
            this.url = url;
        }

        public String getTekst() {
            return tekst;
        }

        public String getUrl() {
            return url;
        }
    }
}
