package no.nav.sbl.dialogarena.minehenvendelser.provider.rs;

import no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/innsendinger")
public class InnsendingerProvider {

    @Inject
    private InnsendingerService innsendingerService;

    @GET
    @Path("/paabegynte")
    @Produces(APPLICATION_JSON)
    public List<Innsending> getPaabegynte() {
        return innsendingerService.getPaabegynte();
    }

    @GET
    @Path("/mottatte")
    @Produces(APPLICATION_JSON)
    public List<Innsending> getMottatte() {
        return innsendingerService.getMottatte();
    }

    @GET
    @Path("/underarbeid")
    @Produces(APPLICATION_JSON)
    public List<Innsending> getUnderArbeid() {
        return innsendingerService.getUnderArbeid();
    }

    @GET
    @Path("/ferdige")
    @Produces(APPLICATION_JSON)
    public List<Innsending> getFerdige() {
        return innsendingerService.getFerdige();
    }

}
