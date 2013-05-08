package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.modig.core.context.Principal;
import no.nav.modig.core.context.SecurityContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;

/**
 * Dette er en standardimplementasjon av grensesnittet som benytter seg av en {@link HenvendelsesBehandlingPortType} implementasjon for å spørre
 * om behandlinger for en aktørId.
 */
public class BehandlingsServicePort implements BehandlingService {

    @Inject
    private HenvendelsesBehandlingPortType service;

    public List<Behandling> hentBehandlinger(String aktoerId){
        SecurityContext.getCurrent().setPrincipal(new Principal.Builder()
                .userId(aktoerId)
                .authenticationLevel("4")
                .consumerId("minehenvendelser")
                .identType("eksternBruker")
                .build());
        List<Behandling> behandlinger = new ArrayList<>();
        for (WSBrukerBehandling wsBrukerBehandling : service.hentBrukerBehandlinger(aktoerId)) {
            behandlinger.add(transformToBehandling(wsBrukerBehandling));
        }
        return behandlinger;
    }

}
