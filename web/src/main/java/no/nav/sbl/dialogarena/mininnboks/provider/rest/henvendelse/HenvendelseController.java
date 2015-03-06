package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.TRAAD_ID;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad.NYESTE_FORST;

@Path("/traader")
@Produces(MediaType.APPLICATION_JSON)
public class HenvendelseController {


    @Inject
    private HenvendelseService henvendelseService;

    @GET
    public List<Traad> hentTraader() {
        String fnr = getSubjectHandler().getUid();
        List<Henvendelse> henvendelser = henvendelseService.hentAlleHenvendelser(fnr);
        final Map<String, List<Henvendelse>> traader = on(henvendelser).reduce(indexBy(TRAAD_ID));

        return on(traader.values()).map(new Transformer<List<Henvendelse>, Traad>() {
            @Override
            public Traad transform(List<Henvendelse> henvendelser) {
                return new Traad(henvendelser);
            }
        }).collect(NYESTE_FORST);
    }

    @GET
    @Path("/{id}")
    public Traad hentTraad(@PathParam("id") String id) {
        return new Traad(henvendelseService.hentTraad(id));
    }

}
