package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding;

import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sporsmal")
@Produces(MediaType.APPLICATION_JSON)
public class SporsmalController {

    @Inject
    private HenvendelseService henvendelseService;

    @GET
    @Path("/ubehandlet")
    public List<UbehandletMelding> ubehandledeMeldinger() {
        return SubjectHandler.getIdent()
                .map(henvendelseService::hentAlleHenvendelser)
                .map(UbehandletMeldingUtils::hentUbehandledeMeldinger)
                .orElseThrow(() -> new ForbiddenException("Fant ikke subjecthandler-ident"));
    }
}
