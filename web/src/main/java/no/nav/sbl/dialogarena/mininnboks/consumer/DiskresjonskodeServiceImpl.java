package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.HentDiskresjonskodeRequest;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.HentDiskresjonskodeResponse;
import org.springframework.stereotype.Service;

import javax.inject.Inject;


@Service
public class DiskresjonskodeServiceImpl implements DiskresjonskodeService {

    @Inject
    private DiskresjonskodePortType portType;

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
