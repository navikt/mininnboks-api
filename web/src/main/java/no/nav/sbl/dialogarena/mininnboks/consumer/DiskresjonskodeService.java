package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.HentDiskresjonskodeRequest;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.HentDiskresjonskodeResponse;

/**
 * Tjeneste for å finne diskresjonskode for en person.
 */
public interface DiskresjonskodeService {
    /**
     * Finn diskresjonskoden for en person.
     *
     * @param fnr fødselsnummer til personen det skal hentes for
     * @return 6/7 for diskresjonskode, blank om ingen kode er satt.
     */
    String getDiskresjonskode(String fnr);

    class Default implements DiskresjonskodeService {

        private final DiskresjonskodePortType portType;

        public Default(DiskresjonskodePortType portType) {
            this.portType = portType;
        }

        public String getDiskresjonskode(String fnr) {
            HentDiskresjonskodeRequest request = createRequest(fnr);
            HentDiskresjonskodeResponse response = portType.hentDiskresjonskode(request);
            return response.getDiskresjonskode();
        }

        private HentDiskresjonskodeRequest createRequest(String fnr) {
            HentDiskresjonskodeRequest request = new HentDiskresjonskodeRequest();
            request.setIdent(fnr);
            return request;
        }
    }
}
