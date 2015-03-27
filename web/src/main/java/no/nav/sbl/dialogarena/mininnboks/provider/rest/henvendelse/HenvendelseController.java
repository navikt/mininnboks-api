package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Svar;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import org.apache.commons.collections15.Transformer;
import org.apache.cxf.binding.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
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

    final Logger logger = LoggerFactory.getLogger(HenvendelseController.class);

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
    public Response hentEnkeltTraad(@PathParam("id") String id) {
        Optional<Traad> traad = hentTraad(id);
        if (traad.isSome()) {
            return Response.ok(traad.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
    }

    @POST
    @Path("/lest/{id}")
    public void markerSomLest(@PathParam("id") String id) {
        henvendelseService.merkSomLest(id);
    }

    @POST
    @Path("/ny")
    @Consumes(APPLICATION_JSON)
    public NyHenvendelseResultat sendSvar(Svar svar, @Context HttpServletResponse httpResponse) {
        assert svar.fritekst.length() > 0 && svar.fritekst.length() <= 1000;
        Optional<Traad> traadOptional = hentTraad(svar.traadId);
        if (!traadOptional.isSome()) {
            httpResponse.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return null;
        }

        Traad traad = traadOptional.get();

        if (!traad.kanBesvares) {
            httpResponse.setStatus(Response.Status.NOT_ACCEPTABLE.getStatusCode());
            return null;
        }

        Henvendelse henvendelse = new Henvendelse(svar.fritekst, traad.nyeste.temagruppe);
        henvendelse.traadId = svar.traadId;
        henvendelse.eksternAktor = traad.nyeste.eksternAktor;
        henvendelse.tilknyttetEnhet = traad.nyeste.tilknyttetEnhet;
        henvendelse.type = SVAR_SBL_INNGAAENDE;
        henvendelse.opprettet = now();
        henvendelse.markerSomLest();
        WSSendInnHenvendelseResponse response = henvendelseService.sendSvar(henvendelse, getSubjectHandler().getUid());

        return new NyHenvendelseResultat(response.getBehandlingsId());
    }

    private Optional<Traad> hentTraad(String id) {
        try {
            List<Henvendelse> meldinger = henvendelseService.hentTraad(id);
            if (meldinger == null || meldinger.isEmpty()) {
                return Optional.none();
            } else {
                return Optional.optional(new Traad(meldinger));
            }
        } catch (SoapFault fault) {
            logger.error("Fant ikke tråd med id: " + id, fault);
            return Optional.none();
        }
    }

    static final class NyHenvendelseResultat {
        public final String behandlingsId;

        private NyHenvendelseResultat(String behandlingsId) {
            this.behandlingsId = behandlingsId;
        }
    }

}