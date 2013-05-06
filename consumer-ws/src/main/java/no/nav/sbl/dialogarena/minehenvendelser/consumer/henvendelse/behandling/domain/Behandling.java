package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;

/**
 * Domeneobjekt som representerer en behandling
 */
public final class Behandling implements Serializable {

    public static final Transformer<Behandling, Behandlingsstatus> STATUS = new Transformer<Behandling, Behandlingsstatus>() {
        @Override
        public Behandlingsstatus transform(Behandling behandling) {
            return behandling.getBehandlingsstatus();
        }
    };
    private static Transformer<WSBrukerBehandling, Behandling> behandlingTransformer = new Transformer<WSBrukerBehandling, Behandling>() {

        @Override
        public Behandling transform(WSBrukerBehandling wsBrukerBehandling) {
            Behandling behandling = new Behandling();
            behandling.dokumentbehandlingstatus = Dokumentbehandlingstatus.valueOf(wsBrukerBehandling.getDokumentbehandlingType().name());
            behandling.behandlingsId = wsBrukerBehandling.getBehandlingsId();
            behandling.hovedskjemaId = wsBrukerBehandling.getHovedskjemaId();
            behandling.behandlingsstatus = Behandlingsstatus.valueOf(wsBrukerBehandling.getStatus().name());
            behandling.sistEndret = wsBrukerBehandling.getSistEndret();
            behandling.innsendtDato = optional(wsBrukerBehandling.getInnsendtDato()).map(dateTimeValueTransformer()).getOrElse(null);
            for (WSDokumentForventningOppsummering wsDokumentForventningOppsummering : wsBrukerBehandling.getDokumentForventningOppsummeringer().getDokumentForventningOppsummering()) {
                behandling.dokumentforventninger.add(Dokumentforventning.transformToDokumentforventing(wsDokumentForventningOppsummering));
            }
            return behandling;
        }

    };
    private String behandlingsId;
    private String hovedskjemaId;
    private DateTime sistEndret;
    private DateTime innsendtDato;
    private Behandlingsstatus behandlingsstatus;
    private Dokumentbehandlingstatus dokumentbehandlingstatus;
    private List<Dokumentforventning> dokumentforventninger = new ArrayList<>();

    private Behandling() {
    }

    private static Transformer<DateTime, DateTime> dateTimeValueTransformer() {
        return new Transformer<DateTime, DateTime>() {
            @Override
            public DateTime transform(DateTime dateTime) {
                return dateTime;
            }
        };
    }

    public static Behandling transformToBehandling(WSBrukerBehandling wsBrukerBehandling) {
        return behandlingTransformer.transform(wsBrukerBehandling);
    }

    public String getHovedskjemaId() {
        return hovedskjemaId;
    }

    public String getBehandlingsId() {
        return behandlingsId;
    }

    public Behandlingsstatus getBehandlingsstatus() {
        return behandlingsstatus;
    }

    public DateTime getSistEndret() {
        return sistEndret;
    }

    public DateTime getInnsendtDato() {
        return innsendtDato;
    }

    public List<Dokumentforventning> getDokumentforventninger() {
        return dokumentforventninger;
    }

    public String getTittel() {
        return fetchHoveddokument().getKodeverkId();
    }

    public int getAntallInnsendteDokumenter() {
        return fetchInnsendteDokumenter().size();
    }

    public int getAntallInnsendteDokumenterUnntattHovedDokument() {
        return fetchInnsendteDokumenterUnntattHovedDokmument().size();
    }

    public int getAntallDokumenterUnntattHovedDokument() {
        return fetchAlleUnntattHovedDokument().size();
    }

    public int getAntallDokumenter() {
        return fetchAlleDokumenter().size();
    }

    public List<Dokumentforventning> fetchInnsendteDokumenter() {
        return on(dokumentforventninger)
                .filter(where(Dokumentforventning.STATUS_LASTET_OPP, equalTo(true)))
                .collect();
    }

    public List<Dokumentforventning> fetchInnsendteDokumenterUnntattHovedDokmument() {
        return on(dokumentforventninger)
                .filter(where(Dokumentforventning.STATUS_LASTET_OPP, equalTo(true)))
                .filter(where(Dokumentforventning.HOVEDSKJEMA, equalTo(false)))
                .collect();
    }

    public List<Dokumentforventning> fetchAlleDokumenter() {
        return on(dokumentforventninger)
                .collect();
    }

    public List<Dokumentforventning> fetchAlleUnntattHovedDokument() {
        return on(dokumentforventninger)
                .filter(where(Dokumentforventning.HOVEDSKJEMA, equalTo(false)))
                .collect();
    }

    public Dokumentforventning fetchHoveddokument() {
        return on(dokumentforventninger)
                .filter(where(Dokumentforventning.HOVEDSKJEMA, equalTo(true)))
                .head().get();
    }

    public Dokumentbehandlingstatus getDokumentbehandlingstatus() {
        return dokumentbehandlingstatus;
    }

    public enum Behandlingsstatus {AVBRUTT_AV_BRUKER, IKKE_SPESIFISERT, UNDER_ARBEID, FERDIG}

    public enum Dokumentbehandlingstatus {SOKNADSINNSENDING, ETTERSENDING}
}
