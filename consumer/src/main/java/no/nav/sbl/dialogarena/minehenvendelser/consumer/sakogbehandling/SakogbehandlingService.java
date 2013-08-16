package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.MOTTATT;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.UNDER_ARBEID;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.transformToSoeknad;

public class SakogbehandlingService {

    @Inject
    @Named("sakOgBehandlingPortType")
    private SakOgBehandlingPortType portType;

    @Inject
    private BehandlingService behandlingService;

    public List<Soeknad> finnSoeknaderUnderArbeid(String aktoerId) {
        try {
            return getSoeknaderUnderArbeid(aktoerId);
        } catch (SOAPFaultException ex) {
            throw new SystemException("Feil ved kall til sak og behandling", ex);
        }
    }

    public List<Soeknad> finnFerdigeSoeknader(String aktoerId) {
        try {
            return getFerdigeSoeknader(aktoerId);
        } catch (SOAPFaultException ex) {
            throw new SystemException("Feil ved kall til sak og behandling", ex);
        }
    }

    public List<Soeknad> finnMottatteSoeknader(String aktoerId) {
        try {
            return getMottatteSoeknader(aktoerId);
        } catch (SOAPFaultException ex) {
            throw new SystemException("Feil ved kall til sak og behandling", ex);
        }
    }

    private List<Soeknad> getMottatteSoeknader(String aktoerId) {
        return evaluateMatches(behandlingService.hentFerdigeBehandlinger(aktoerId), evaluateUkjentStatusBehandlingskjeder(populateBehandlingskjedeList(aktoerId)));
    }

    private List<Soeknad> getFerdigeSoeknader(String aktoerId) {
        List<Soeknad> soeknadListe = new ArrayList<>();
        for (Sak sak : portType.finnSakOgBehandlingskjedeListe(createRequest(aktoerId)).getSak()) {
            for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
                if (soeknadHasStatusFerdig(behandlingskjede)) {
                    soeknadListe.add(transformToSoeknad(behandlingskjede, FERDIG));
                }
            }
        }
        return soeknadListe;
    }

    private boolean soeknadHasStatusFerdig(Behandlingskjede behandlingskjede) {
        return behandlingskjede.getSluttNAVtid() != null;
    }

    private List<Soeknad> getSoeknaderUnderArbeid(String aktoerId) {
        List<Soeknad> soeknadListe = new ArrayList<>();
        for (Sak sak : portType.finnSakOgBehandlingskjedeListe(createRequest(aktoerId)).getSak()) {
            for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
                if (soeknadHasStatusUnderArbeid(behandlingskjede)) {
                    soeknadListe.add(transformToSoeknad(behandlingskjede, UNDER_ARBEID));
                }
            }
        }
        return soeknadListe;
    }

    private List<Soeknad> evaluateMatches(List<Henvendelsesbehandling> henvendelsesbehandlinger, List<Behandlingskjede> potentiallyMottatteSoeknader) {
        List<Soeknad> mottatteSoeknader = new ArrayList<>();
        for (Behandlingskjede behandlingskjede : potentiallyMottatteSoeknader) {
            if (behandlingskjedeMatchesHenvendelsesBehandling(behandlingskjede, henvendelsesbehandlinger)) {
                if (firstBehandlingIsUnfinished(behandlingskjede)) {
                    mottatteSoeknader.add(transformToSoeknad(behandlingskjede, MOTTATT));
                }
            }
        }
        return mottatteSoeknader;
    }

    private List<Behandlingskjede> evaluateUkjentStatusBehandlingskjeder(List<Behandlingskjede> behandlingskjedeList) {
        List<Behandlingskjede> ukjentStatusBehandlinger = new ArrayList<>();
        for (Behandlingskjede behandlingskjede : behandlingskjedeList) {
            if (behandlingskjedeIsNeitherUnderArbeidNorFinished(behandlingskjede)) {
                ukjentStatusBehandlinger.add(behandlingskjede);
            }
        }
        return ukjentStatusBehandlinger;
    }

    private List<Behandlingskjede> populateBehandlingskjedeList(String aktoerId) {
        List<Behandlingskjede> behandlingskjedeList = new ArrayList<>();
        for (Sak sak : portType.finnSakOgBehandlingskjedeListe(createRequest(aktoerId)).getSak()) {
            for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
                behandlingskjedeList.add(behandlingskjede);
            }
        }
        return behandlingskjedeList;
    }

    private boolean firstBehandlingIsUnfinished(Behandlingskjede behandlingskjede) {
        return portType.hentBehandlingskjedensBehandlinger(createRequest(behandlingskjede)).getBehandlingskjede().getBehandling().get(0).getBehandlingsstatus().getValue().equals("UNFINISHED");
    }

    private boolean behandlingskjedeMatchesHenvendelsesBehandling(Behandlingskjede behandlingskjede, List<Henvendelsesbehandling> henvendelsesbehandlingList) {
        List<Behandling> potensielleBehandlinger = portType.hentBehandlingskjedensBehandlinger(createRequest(behandlingskjede)).getBehandlingskjede().getBehandling();
        for (Henvendelsesbehandling henvendelsesbehandling : henvendelsesbehandlingList) {
            for (Behandling behandling : potensielleBehandlinger) {
                if (henvendelsesbehandling.getBehandlingsId().equals(behandling.getBehandlingsId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private HentBehandlingskjedensBehandlingerRequest createRequest(Behandlingskjede behandlingskjede) {
        return new HentBehandlingskjedensBehandlingerRequest().withBehandlingskjedeREF(behandlingskjede.getBehandlingskjedeId());
    }

    private boolean behandlingskjedeIsNeitherUnderArbeidNorFinished(Behandlingskjede behandlingskjede) {
        return behandlingskjede.getSluttNAVtid() == null && behandlingskjede.getStartNAVtid() == null;
    }

    private FinnSakOgBehandlingskjedeListeRequest createRequest(String aktoerId) {
        return new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId);
    }

    private boolean soeknadHasStatusUnderArbeid(Behandlingskjede behandlingskjede) {
        return behandlingskjede.getSluttNAVtid() == null && behandlingskjede.getStartNAVtid() != null;
    }

}
