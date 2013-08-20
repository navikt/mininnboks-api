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
        return findMatches(behandlingService.hentFerdigeBehandlinger(aktoerId), behandlingskjederUtenStartEllerSluttNAVtid(populateBehandlingskjedeList(aktoerId)));
    }

    private List<Soeknad> getFerdigeSoeknader(String aktoerId) {
        List<Soeknad> soeknadListe = new ArrayList<>();
        for (Sak sak : getSak(aktoerId)) {
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
        for (Sak sak : getSak(aktoerId)) {
            for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
                if (behandlingskjedeHasStatusUnderArbeid(behandlingskjede)) {
                    soeknadListe.add(transformToSoeknad(behandlingskjede, UNDER_ARBEID));
                }
            }
        }
        return soeknadListe;
    }

    private List<Soeknad> findMatches(List<Henvendelsesbehandling> ferdigeHenvendelsesbehandlinger, List<Behandlingskjede> soeknaderUtenStartOrSluttNAVtid) {
        List<Soeknad> mottatteSoeknader = new ArrayList<>();
        for (Behandlingskjede behandlingskjede : soeknaderUtenStartOrSluttNAVtid) {
            if (behandlingskjedeMatchesHenvendelsesBehandling(behandlingskjede, ferdigeHenvendelsesbehandlinger)) {
                mottatteSoeknader.add(transformToSoeknad(behandlingskjede, MOTTATT));
            }
        }
        return mottatteSoeknader;
    }

    private List<Behandlingskjede> behandlingskjederUtenStartEllerSluttNAVtid(List<Behandlingskjede> behandlingskjedeList) {
        List<Behandlingskjede> behandlingskjederUtenStartEllerSluttNAVtid = new ArrayList<>();
        for (Behandlingskjede behandlingskjede : behandlingskjedeList) {
            if (behandlingskjedeHarHverkenStartEllerSluttNAVtid(behandlingskjede)) {
                behandlingskjederUtenStartEllerSluttNAVtid.add(behandlingskjede);
            }
        }
        return behandlingskjederUtenStartEllerSluttNAVtid;
    }

    private List<Behandlingskjede> populateBehandlingskjedeList(String aktoerId) {
        List<Behandlingskjede> behandlingskjedeList = new ArrayList<>();
        for (Sak sak : getSak(aktoerId)) {
            for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
                behandlingskjedeList.add(behandlingskjede);
            }
        }
        return behandlingskjedeList;
    }

    private List<Sak> getSak(String aktoerId) {
        return portType.finnSakOgBehandlingskjedeListe(createRequest(aktoerId)).getSak();
    }

    private boolean behandlingskjedeMatchesHenvendelsesBehandling(Behandlingskjede behandlingskjede, List<Henvendelsesbehandling> henvendelsesbehandlingList) {
        for (Henvendelsesbehandling henvendelsesbehandling : henvendelsesbehandlingList) {
            for (Behandling behandling : getBehandlingskjedensBehandlinger(behandlingskjede)) {
                if (behandlingsIdIsTheSame(henvendelsesbehandling, behandling)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Behandling> getBehandlingskjedensBehandlinger(Behandlingskjede behandlingskjede) {
        return portType.hentBehandlingskjedensBehandlinger(createRequest(behandlingskjede)).getBehandlingskjede().getBehandling();
    }

    private boolean behandlingsIdIsTheSame(Henvendelsesbehandling henvendelsesbehandling, Behandling behandling) {
        return henvendelsesbehandling.getBehandlingsId().equals(behandling.getBehandlingsId());
    }

    private HentBehandlingskjedensBehandlingerRequest createRequest(Behandlingskjede behandlingskjede) {
        return new HentBehandlingskjedensBehandlingerRequest().withBehandlingskjedeREF(behandlingskjede.getBehandlingskjedeId());
    }

    private boolean behandlingskjedeHarHverkenStartEllerSluttNAVtid(Behandlingskjede behandlingskjede) {
        return behandlingskjede.getSluttNAVtid() == null && behandlingskjede.getStartNAVtid() == null;
    }

    private FinnSakOgBehandlingskjedeListeRequest createRequest(String aktoerId) {
        return new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId);
    }

    private boolean behandlingskjedeHasStatusUnderArbeid(Behandlingskjede behandlingskjede) {
        return behandlingskjede.getSluttNAVtid() == null && behandlingskjede.getStartNAVtid() != null;
    }

}
