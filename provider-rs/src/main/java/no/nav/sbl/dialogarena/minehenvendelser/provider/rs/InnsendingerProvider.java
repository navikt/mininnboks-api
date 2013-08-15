package no.nav.sbl.dialogarena.minehenvendelser.provider.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class InnsendingerProvider {

    @GET
    @Path("/innsendinger")
    public String getInnsendinger() {
        return "Hello world!";
    }
}
