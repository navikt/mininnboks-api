package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*;
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
import javax.xml.ws.soap.SOAPFaultException;
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
    @Path("/sporsmal")
    @Consumes(APPLICATION_JSON)
    public Response sendSporsmal(Sporsmal sporsmal, @Context HttpServletResponse httpResponse) {
        assertFritekst(sporsmal.fritekst);
        assertTemagruppe(sporsmal.temagruppe);

        Temagruppe temagruppe = Temagruppe.valueOf(sporsmal.temagruppe);
        Henvendelse henvendelse = new Henvendelse(sporsmal.fritekst, temagruppe);

        WSSendInnHenvendelseResponse response = henvendelseService.stillSporsmal(henvendelse, getSubjectHandler().getUid());
        return Response.status(Response.Status.CREATED).entity(new NyHenvendelseResultat(response.getBehandlingsId())).build();
    }

    @POST
    @Path("/svar")
    @Consumes(APPLICATION_JSON)
    public Response sendSvar(Svar svar) {
        assertFritekst(svar.fritekst);
        Optional<Traad> traadOptional = hentTraad(svar.traadId);
        if (!traadOptional.isSome()) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }

        Traad traad = traadOptional.get();

        if (!traad.kanBesvares) {
            return Response.status(Response.Status.NOT_ACCEPTABLE.getStatusCode()).build();
        }

        Henvendelse henvendelse = new Henvendelse(svar.fritekst, traad.nyeste.temagruppe);
        henvendelse.traadId = svar.traadId;
        henvendelse.eksternAktor = traad.nyeste.eksternAktor;
        henvendelse.brukersEnhet = traad.eldste.brukersEnhet;
        henvendelse.tilknyttetEnhet = traad.nyeste.tilknyttetEnhet;
        henvendelse.type = SVAR_SBL_INNGAAENDE;
        henvendelse.opprettet = now();
        henvendelse.markerSomLest();
        henvendelse.erTilknyttetAnsatt = traad.nyeste.erTilknyttetAnsatt;

        WSSendInnHenvendelseResponse response = henvendelseService.sendSvar(henvendelse, getSubjectHandler().getUid());
        return Response.status(Response.Status.CREATED).entity(new NyHenvendelseResultat(response.getBehandlingsId())).build();
    }

    private Optional<Traad> hentTraad(String id) {
        try {
            List<Henvendelse> meldinger = henvendelseService.hentTraad(id);
            if (meldinger == null || meldinger.isEmpty()) {
                return Optional.none();
            } else {
                return Optional.optional(new Traad(meldinger));
            }
        } catch (SoapFault | SOAPFaultException fault) {
            logger.error("Fant ikke tråd med id: " + id, fault);
            return Optional.none();
        }
    }

    private static void assertFritekst(String fritekst) {
        assert fritekst.length() > 0 && fritekst.length() <= 1000;
    }

    private static void assertTemagruppe(String temagruppe) {
        assert Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL.contains(Temagruppe.valueOf(temagruppe));
    }

    static final class NyHenvendelseResultat {
        public final String behandlingsId;

        private NyHenvendelseResultat(String behandlingsId) {
            this.behandlingsId = behandlingsId;
        }
    }


}