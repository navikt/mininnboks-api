package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;

import javax.annotation.Resource;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;

/**
 * Dette er en standardimplementasjon av grensesnittet som benytter seg av en {@link HenvendelsesBehandlingPortType} implementasjon for å spørre
 * om behandlinger for en aktørId.
 */
public class BehandlingsServicePort implements BehandlingService {

    @Resource
//    @Named("henvendelsesBehandlingPortType")
    private HenvendelsesBehandlingPortType service;

    public List<Behandling> hentBehandlinger(String aktoerId){
        List<Behandling> behandlinger = new ArrayList<>();
        if (aktoerId != null) {
            try {
                for (WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering : service.hentBrukerBehandlinger(aktoerId)) {
                    behandlinger.add(transformToBehandling(wsBrukerBehandlingOppsummering));
                }
            } catch (SOAPFaultException ex){
                throw new SystemException("Feil ved kall til henvendelse", ex);
            }
        }
        return behandlinger;
    }

}
