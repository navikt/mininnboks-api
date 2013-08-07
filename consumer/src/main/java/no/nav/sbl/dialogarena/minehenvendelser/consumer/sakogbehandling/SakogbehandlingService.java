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

public class SakogbehandlingService {

    @Inject
    @Named("sakOgBehandlingPortType")
    private SakOgBehandlingPortType portType;

    public List<Soeknad> hentSaker(String aktoerId) {
        if (aktoerId != null) {
            try {
                return getAlleSoeknader(aktoerId);
            } catch (SOAPFaultException ex) {
                throw new SystemException("Feil ved kall til sak og behandling", ex);
            }
        }
        return null;
    }

    private List<Soeknad> getAlleSoeknader(String aktoerId) {
        List<Soeknad> soeknadListe = new ArrayList<>();
        for (Sak sak : portType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId)).getSak()) {
            for (Behandlingskjede behandlingskjede : sak.getBehandlingskjede()) {
                soeknadListe.add(Soeknad.transformToSoeknad(behandlingskjede));
            }
        }
        return soeknadListe;
    }
}
