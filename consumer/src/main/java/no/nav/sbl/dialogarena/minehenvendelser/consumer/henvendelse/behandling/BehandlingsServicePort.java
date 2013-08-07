package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.BEHANDLINGSSTATUS_TRANSFORMER;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;

/**
 * Dette er en standardimplementasjon av grensesnittet som benytter seg av en {@link HenvendelsesBehandlingPortType} implementasjon for å spørre
 * om behandlinger for en aktørId.
 */
public class BehandlingsServicePort implements BehandlingService {

    @Inject
    @Named("getHenvendelsesBehandlingPortType")
    private HenvendelsesBehandlingPortType portType;

    @Override
    public List<Behandling> hentBehandlinger(String foedselsnummer){
        List<Behandling> behandlinger = new ArrayList<>();
        if (foedselsnummer != null) {
            try {
                for (WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering : portType.hentBrukerBehandlingListe(foedselsnummer)) {
                    behandlinger.add(transformToBehandling(wsBrukerBehandlingOppsummering));
                }
            } catch (SOAPFaultException ex){
                throw new SystemException("Feil ved kall til henvendelse", ex);
            }
        }
        return behandlinger;
    }

    @Override
    public List<Behandling> hentPabegynteBehandlinger(String foedselsnummer) {
        return on(hentBehandlinger(foedselsnummer)).filter(where(BEHANDLINGSSTATUS_TRANSFORMER, equalTo(Behandlingsstatus.UNDER_ARBEID))).collect();
    }
}
