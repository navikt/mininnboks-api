package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.transformToSoeknad;

public class SakogbehandlingService {

    @Inject
    @Named("sakOgBehandlingPortType")
    private SakOgBehandlingPortType portType;

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

    private List<Soeknad> getFerdigeSoeknader(String aktoerId) {
        List<Soeknad> soeknadListe = new ArrayList<>();
        for (Sak sak : portType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId)).getSak()) {
            for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
                if (soeknadHasStatusFerdig(behandlingskjede)) {
                    soeknadListe.add(transformToSoeknad(behandlingskjede));
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
        for (Sak sak : portType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId)).getSak()) {
            for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
                if (soeknadHasStatusUnderArbeid(behandlingskjede)) {
                    soeknadListe.add(transformToSoeknad(behandlingskjede));
                }
            }
        }
        return soeknadListe;
    }

    private boolean soeknadHasStatusUnderArbeid(Behandlingskjede behandlingskjede) {
        return behandlingskjede.getSluttNAVtid() == null && behandlingskjede.getStartNAVtid() != null;
    }

}
