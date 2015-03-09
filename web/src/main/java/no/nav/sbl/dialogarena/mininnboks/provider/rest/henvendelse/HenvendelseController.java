package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Svar;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.TRAAD_ID;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SVAR_SBL_INNGAAENDE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad.NYESTE_FORST;
import static org.joda.time.DateTime.now;

@Path("/traader")
@Produces(APPLICATION_JSON)
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

    @POST
    @Path("/lest/{id}")
    public void markerSomLest(@PathParam("id") String id) {
        henvendelseService.merkSomLest(id);
    }

    @POST
    @Path("/ny")
    @Consumes(APPLICATION_JSON)
    public String sendSvar(Svar svar) {
        assert svar.fritekst.length() > 0 && svar.fritekst.length() <= 1000;
        Traad traad = hentTraad(svar.traadId);

        Henvendelse henvendelse = new Henvendelse(svar.fritekst, traad.nyeste.temagruppe);
        henvendelse.traadId = svar.traadId;
        henvendelse.eksternAktor = traad.nyeste.eksternAktor;
        henvendelse.tilknyttetEnhet = traad.nyeste.tilknyttetEnhet;
        henvendelse.type = SVAR_SBL_INNGAAENDE;
        henvendelse.opprettet = now();
        henvendelse.markerSomLest();
        WSSendInnHenvendelseResponse response = henvendelseService.sendSvar(henvendelse, getSubjectHandler().getUid());

        return response.getBehandlingsId();
    }

}