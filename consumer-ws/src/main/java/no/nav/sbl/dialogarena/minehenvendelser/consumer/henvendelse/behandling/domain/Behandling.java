package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
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
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Dokumentbehandlingstatus.DOKUMENT_ETTERSENDING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.STATUS_LASTET_OPP;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.transformToDokumentforventing;

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
    private static final String KODEVERKSID_FOR_KVITTERING = "L7";

    private static Transformer<WSBrukerBehandlingOppsummering, Behandling> behandlingTransformer = new Transformer<WSBrukerBehandlingOppsummering, Behandling>() {

        @Override
        public Behandling transform(WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering) {
            Behandling behandling = new Behandling();
            behandling.behandlingsId = wsBrukerBehandlingOppsummering.getBehandlingsId();
            behandling.hovedskjemaId = wsBrukerBehandlingOppsummering.getHovedskjemaId();
            behandling.behandlingsstatus = Behandlingsstatus.valueOf(wsBrukerBehandlingOppsummering.getStatus().name());
            behandling.dokumentbehandlingstatus = Dokumentbehandlingstatus.valueOf(wsBrukerBehandlingOppsummering.getBrukerBehandlingType().name());
            behandling.sistEndret = wsBrukerBehandlingOppsummering.getSistEndret();
            behandling.innsendtDato = optional(wsBrukerBehandlingOppsummering.getInnsendtDato()).map(dateTimeValueTransformer()).getOrElse(null);
            addForventningerToBehandling(wsBrukerBehandlingOppsummering, behandling);
            return behandling;
        }

        private void addForventningerToBehandling(WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering, Behandling behandling) {
            for (WSDokumentForventningOppsummering wsDokumentForventningOppsummering : wsBrukerBehandlingOppsummering.getDokumentForventningOppsummeringer().getDokumentForventningOppsummering()) {
                behandling.dokumentforventninger.add(transformToDokumentforventing(wsDokumentForventningOppsummering));
            }
        }

    };

    private String behandlingsId;
    private String hovedskjemaId;
    private DateTime sistEndret;
    private DateTime innsendtDato;
    private Behandlingsstatus behandlingsstatus;
    private Dokumentbehandlingstatus dokumentbehandlingstatus;
    private List<Dokumentforventning> dokumentforventninger = new ArrayList<>();

    private Behandling() { }

    private static Transformer<DateTime, DateTime> dateTimeValueTransformer() {
        return new Transformer<DateTime, DateTime>() {
            @Override
            public DateTime transform(DateTime dateTime) {
                return dateTime;
            }
        };
    }

    public static Behandling transformToBehandling(WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering) {
        return behandlingTransformer.transform(wsBrukerBehandlingOppsummering);
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

    public String getKodeverkId() {
        return fetchHoveddokument().getKodeverkId();
    }

    public List<Dokumentforventning> fetchInnsendteDokumenter() {
        List<Dokumentforventning> dokumentforventningsList = on(dokumentforventninger)
                .filter(where(STATUS_LASTET_OPP, equalTo(true)))
                .collect();
        for (int i = 0; i < dokumentforventningsList.size(); i++) {
            if (KODEVERKSID_FOR_KVITTERING.equals(dokumentforventningsList.get(i).getKodeverkId())) {
                dokumentforventningsList.remove(i);
                break;
            }
        }
        return dokumentforventningsList;
    }

    public boolean hasManglendeDokumenter() {
        return on(dokumentforventninger)
                .filter(where(STATUS_LASTET_OPP, equalTo(false)))
                .collect().size() != 0;
    }

    public List<Dokumentforventning> fetchInnsendteDokumenterUnntattHovedDokument() {
        return on(fetchInnsendteDokumenter())
                .filter(where(STATUS_LASTET_OPP, equalTo(true)))
                .filter(where(HOVEDSKJEMA, equalTo(false)))
                .collect();
    }

    public List<Dokumentforventning> fetchAlleDokumenter() {
        return on(dokumentforventninger)
                .collect();
    }

    public Dokumentforventning fetchHoveddokument() {
        return on(dokumentforventninger)
                .filter(where(HOVEDSKJEMA, equalTo(true)))
                .head().get();
    }

    public Dokumentbehandlingstatus getDokumentbehandlingstatus() {
        return dokumentbehandlingstatus;
    }

    public enum Behandlingsstatus {AVBRUTT_AV_BRUKER, IKKE_SPESIFISERT, UNDER_ARBEID, FERDIG}

    public enum Dokumentbehandlingstatus {DOKUMENT_BEHANDLING, DOKUMENT_ETTERSENDING}

    public List<Dokumentforventning> getRelevanteDokumenter() {
        List<Dokumentforventning> returnList;
        if (dokumentbehandlingstatus == DOKUMENT_ETTERSENDING) {
            returnList = on(dokumentforventninger)
                    .filter(where(HOVEDSKJEMA, equalTo(false)))
                    .collect();
        } else {
            returnList = fetchAlleDokumenter();
        }
        return returnList;
    }
}
