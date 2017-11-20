package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.metrics.MetricsFactory;
import no.nav.metrics.Timer;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import org.apache.cxf.binding.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.groupingBy;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
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

        Timer timer = MetricsFactory.createTimer("mininnboks.henttrader.responstid");
        timer.start();
        List<Henvendelse> henvendelser = henvendelseService.hentAlleHenvendelser(fnr);
        timer.stop();
        timer.report();

        final Map<String, List<Henvendelse>> traader = henvendelser.stream()
                .collect(groupingBy(henvendelse -> henvendelse.traadId));

        return traader.values().stream()
                .map(henvendelse -> new Traad(henvendelse))
                .sorted(NYESTE_FORST)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response hentEnkeltTraad(@PathParam("id") String id) {
        Optional<Traad> traad = hentTraad(id);
        if (traad.isPresent()) {
            return ok(traad.get()).build();
        } else {
            return status(NOT_FOUND.getStatusCode()).build();
        }
    }

    @POST
    @Path("/lest/{behandlingsId}")
    public Response markerSomLest(@PathParam("behandlingsId") String behandlingsId) {
        henvendelseService.merkSomLest(behandlingsId);
        return ok(TupleResultat.of("traadId", behandlingsId)).build();
    }

    @POST
    @Path("/allelest/{behandlingskjedeId}")
    public Response markerAlleSomLest(@PathParam("behandlingskjedeId") String behandlingskjedeId) {
        henvendelseService.merkAlleSomLest(behandlingskjedeId);
        return ok(TupleResultat.of("traadId", behandlingskjedeId)).build();
    }

    @POST
    @Path("/sporsmal")
    @Consumes(APPLICATION_JSON)
    public Response sendSporsmal(Sporsmal sporsmal) {
        assertFritekst(sporsmal.fritekst);
        assertTemagruppe(sporsmal.temagruppe);

        Temagruppe temagruppe = Temagruppe.valueOf(sporsmal.temagruppe);
        Henvendelse henvendelse = new Henvendelse(sporsmal.fritekst, temagruppe);

        MetricsFactory.createEvent("mininnboks.sendsporsmal").report();
        WSSendInnHenvendelseResponse response = henvendelseService.stillSporsmal(henvendelse, getSubjectHandler().getUid());

        return status(CREATED).entity(new NyHenvendelseResultat(response.getBehandlingsId())).build();
    }

    @POST
    @Path("/svar")
    @Consumes(APPLICATION_JSON)
    public Response sendSvar(Svar svar) {
        assertFritekst(svar.fritekst);
        Optional<Traad> traadOptional = hentTraad(svar.traadId);
        if (!traadOptional.isPresent()) {
            return status(NOT_FOUND.getStatusCode()).build();
        }

        Traad traad = traadOptional.get();

        if (!traad.kanBesvares) {
            return status(NOT_ACCEPTABLE.getStatusCode()).build();
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
        henvendelse.kontorsperreEnhet = traad.nyeste.kontorsperreEnhet;

        MetricsFactory.createEvent("mininnboks.sendsvar").report();
        WSSendInnHenvendelseResponse response = henvendelseService.sendSvar(henvendelse, getSubjectHandler().getUid());

        return status(CREATED).entity(new NyHenvendelseResultat(response.getBehandlingsId())).build();
    }

    private Optional<Traad> hentTraad(String id) {
        try {
            List<Henvendelse> meldinger = henvendelseService.hentTraad(id);
            if (meldinger == null || meldinger.isEmpty()) {
                return empty();
            } else {
                return of(new Traad(meldinger));
            }
        } catch (SoapFault | SOAPFaultException fault) {
            logger.error("Fant ikke tråd med id: " + id, fault);
            return empty();
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

    static final class TupleResultat extends HashMap<String, String> {
        public static TupleResultat of(String key, String value) {
            TupleResultat tr = new TupleResultat();
            tr.put(key, value);
            return tr;
        }
    }

}