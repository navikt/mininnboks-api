package no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang;

import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO;
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/tilgang")
@Produces(APPLICATION_JSON + "; charset=UTF-8")
public class TilgangController {

    @Inject
    private TilgangService tilgangService;

    @GET
    @Path("/oksos")
    public TilgangDTO harTilgangTilKommunalInnsending() {
        return SubjectHandler
                .getIdent()
                .map((fnr) -> tilgangService.harTilgangTilKommunalInnsending(fnr))
                .orElse(new TilgangDTO(TilgangDTO.Resultat.FEILET, "Fant ikke brukers OIDC-token"));
    }
}
