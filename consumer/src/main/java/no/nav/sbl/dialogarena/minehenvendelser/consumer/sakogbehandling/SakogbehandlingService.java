package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.MHSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;

import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.List;

public class SakogbehandlingService {

    @Inject
    private SakOgBehandlingPortType service;

    public List<MHSak> hentSaker(String aktoerId) {
        if (aktoerId != null) {
            try {
                //getSak() returnerer her en liste med saker (!). Burde hete getSakListe e.l.?
                List<MHSak> mHSakListe = new ArrayList<>();
                for (Sak sak : service.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktoerId)).getSak()) {
                    mHSakListe.add(MHSak.transformToIPSak(sak));
                }
                return mHSakListe;
            } catch (SOAPFaultException ex) {
                throw new SystemException("Feil ved kall til sak og behandling", ex);
            }
        }
        return null;
    }
}
