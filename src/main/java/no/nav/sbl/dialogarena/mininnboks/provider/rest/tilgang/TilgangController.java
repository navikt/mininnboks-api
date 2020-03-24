package no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang;

import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/tilgang")
@Produces(APPLICATION_JSON + "; charset=UTF-8")
public class TilgangController {

    @Inject
    private PdlService pdlService;

    @GET
    @Path("/oksos")
    public Boolean harTilgangTilKommunalInnsending() {
        String fnr = SubjectHandler.getIdent().orElseThrow(() -> new NotAuthorizedException("Fant ikke brukers OIDC-token"));
        return !pdlService.harKode6(fnr);
    }
}
