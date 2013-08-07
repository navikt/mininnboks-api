package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;

import java.util.List;

/**
 * Dette grensesnittet definerer adgang til å hente ut behandlinger. Det er applikasjonens hovedgrensesnitt utad og en implementasjon
 * kontakter implisitt en ekstern webtjeneste, som definert i {@link no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType}
 */
public interface BehandlingService {

    /**
     * Henter ut en liste over behandlinger som er knyttet til en aktørId. Parameteren hentes ut fra innloggings/sikkerhetskontekst implisitt
     * @param foedselsnummer unik identifikator for en aktør.
     * @return En liste med behandlinger knyttet til aktøren
     */
    List<Behandling> hentBehandlinger(String foedselsnummer);
    List<Behandling> hentPabegynteBehandlinger(String foedselsnummer);

}
