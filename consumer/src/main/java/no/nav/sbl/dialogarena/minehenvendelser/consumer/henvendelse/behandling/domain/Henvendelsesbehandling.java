package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling.Dokumentbehandlingstatus.DOKUMENT_ETTERSENDING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.STATUS_LASTET_OPP;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.transformToDokumentforventing;

/**
 * Domeneobjekt som representerer en behandling
 */
public final class Henvendelsesbehandling implements Serializable {

    public enum Behandlingsstatus {AVBRUTT_AV_BRUKER, IKKE_SPESIFISERT, UNDER_ARBEID, FERDIG}

    public enum Dokumentbehandlingstatus {DOKUMENT_BEHANDLING, DOKUMENT_ETTERSENDING}

    private static final String KODEVERKSID_FOR_KVITTERING = "L7";

    private String behandlingsId;
    private String hovedskjemaId;
    private DateTime sistEndret;
    private DateTime innsendtDato;
    private Behandlingsstatus behandlingsstatus;
    private Dokumentbehandlingstatus dokumentbehandlingstatus;
    private List<Dokumentforventning> dokumentforventninger = new ArrayList<>();

    private Henvendelsesbehandling() { }

    public static final Transformer<Henvendelsesbehandling, Behandlingsstatus> BEHANDLINGSSTATUS_TRANSFORMER = new Transformer<Henvendelsesbehandling, Behandlingsstatus>() {
        @Override
        public Behandlingsstatus transform(Henvendelsesbehandling henvendelsesbehandling) {
            return henvendelsesbehandling.getBehandlingsstatus();
        }
    };

    public static Henvendelsesbehandling transformToBehandling(WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering) {
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

    public boolean hasManglendeDokumenter() {
        return on(dokumentforventninger).filter(where(STATUS_LASTET_OPP, equalTo(false))).collect().size() != 0;
    }

    public Dokumentforventning fetchHoveddokument() {
        return on(dokumentforventninger).filter(where(HOVEDSKJEMA, equalTo(true))).head().get();
    }

    public Dokumentbehandlingstatus getDokumentbehandlingstatus() {
        return dokumentbehandlingstatus;
    }

    public List<Dokumentforventning> getInnsendteDokumenter(boolean excludeHoveddokument) {
        List<Dokumentforventning> returnList = on(clearKvittering()).filter(where(STATUS_LASTET_OPP, equalTo(true))).collect();
        if (excludeHoveddokument) {
               returnList = on(returnList).filter(where(HOVEDSKJEMA, equalTo(false))).collect();
        }
        return returnList;
    }

    public List<Dokumentforventning> getRelevanteDokumenter() {
        List<Dokumentforventning> returnList = clearKvittering();
        if (dokumentbehandlingstatus == DOKUMENT_ETTERSENDING) {
            returnList = on(returnList).filter(where(HOVEDSKJEMA, equalTo(false))).collect();
        }
        return returnList;
    }

    private List<Dokumentforventning> clearKvittering() {
        List<Dokumentforventning> dokumentforventningsList = dokumentforventninger;
        for (int i = 0; i < dokumentforventningsList.size(); i++) {
            if (KODEVERKSID_FOR_KVITTERING.equals(dokumentforventningsList.get(i).getKodeverkId())) {
                dokumentforventningsList.remove(i);
                break;
            }
        }
        return dokumentforventningsList;
    }

    private static Transformer<WSBrukerBehandlingOppsummering, Henvendelsesbehandling> behandlingTransformer = new Transformer<WSBrukerBehandlingOppsummering, Henvendelsesbehandling>() {

        @Override
        public Henvendelsesbehandling transform(WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering) {
            Henvendelsesbehandling henvendelsesbehandling = new Henvendelsesbehandling();
            henvendelsesbehandling.behandlingsId = wsBrukerBehandlingOppsummering.getBehandlingsId();
            henvendelsesbehandling.hovedskjemaId = wsBrukerBehandlingOppsummering.getHovedskjemaId();
            henvendelsesbehandling.behandlingsstatus = Behandlingsstatus.valueOf(wsBrukerBehandlingOppsummering.getStatus().name());
            henvendelsesbehandling.dokumentbehandlingstatus = Dokumentbehandlingstatus.valueOf(wsBrukerBehandlingOppsummering.getBrukerBehandlingType().name());
            henvendelsesbehandling.sistEndret = wsBrukerBehandlingOppsummering.getSistEndret();
            henvendelsesbehandling.innsendtDato = optional(wsBrukerBehandlingOppsummering.getInnsendtDato()).map(dateTimeValueTransformer()).getOrElse(null);
            addForventningerToBehandling(wsBrukerBehandlingOppsummering, henvendelsesbehandling);
            return henvendelsesbehandling;
        }

        private void addForventningerToBehandling(WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering, Henvendelsesbehandling henvendelsesbehandling) {
            for (WSDokumentForventningOppsummering wsDokumentForventningOppsummering : wsBrukerBehandlingOppsummering.getDokumentForventningOppsummeringer().getDokumentForventningOppsummering()) {
                henvendelsesbehandling.dokumentforventninger.add(transformToDokumentforventing(wsDokumentForventningOppsummering));
            }
        }

    };


    private static Transformer<DateTime, DateTime> dateTimeValueTransformer() {
        return new Transformer<DateTime, DateTime>() {
            @Override
            public DateTime transform(DateTime dateTime) {
                return dateTime;
            }
        };
    }

}
