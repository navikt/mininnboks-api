package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

@Path("/sporsmal")
@Produces(MediaType.APPLICATION_JSON)
public class SporsmalController {

    @Inject
    private HenvendelseService henvendelseService;

    @GET
    @Path("/ubehandlet")
    public List<SporsmalVarsel> ulesteSporsmal() {
        return SporsmalVarselUtils.hentUbehandledeSporsmal(henvendelseService.hentAlleHenvendelser(getSubjectHandler().getUid()));
    }
}
