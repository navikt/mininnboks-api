package no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang;

import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.Try;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/tilgang")
@Produces(APPLICATION_JSON + "; charset=UTF-8")
public class TilgangController {

    @Inject
    private PdlService pdlService;

    @Inject
    private PersonService personService;

    @GET
    @Path("/oksos")
    public TilgangDTO harTilgangTilKommunalInnsending() {
        return SubjectHandler
                .getIdent()
                .map((fnr) -> {
                    Try<Boolean> harEnhet = Try.of(() -> personService.hentEnhet().isPresent());
                    Try<Boolean> harKode6 = Try.of(() -> pdlService.harKode6(fnr));

                    if (harEnhet.isFailure()) {
                        return new TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers enhet: " + harEnhet.getFailure().getMessage());
                    } else if (!harEnhet.get()) {
                        return new TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "Bruker har ingen enhet");
                    }

                    if (harKode6.isFailure()) {
                        return new TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers diskresjonskode: " + harKode6.getFailure().getMessage());
                    } else if (harKode6.get()) {
                        return new TilgangDTO(TilgangDTO.Resultat.KODE6, "Bruker har diskresjonskode");
                    }

                    return new TilgangDTO(TilgangDTO.Resultat.OK, "");
                })
                .orElse(new TilgangDTO(TilgangDTO.Resultat.FEILET, "Fant ikke brukers OIDC-token"));
    }
}
