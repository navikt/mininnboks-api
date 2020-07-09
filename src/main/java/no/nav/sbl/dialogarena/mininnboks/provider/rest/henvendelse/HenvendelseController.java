package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.common.auth.SubjectHandler;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*;
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO;
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import org.apache.cxf.binding.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.*;
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
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SVAR_SBL_INNGAAENDE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad.NYESTE_FORST;

@Path("/traader")
@Produces(APPLICATION_JSON)
public class HenvendelseController {

    final Logger logger = LoggerFactory.getLogger(HenvendelseController.class);

    @Inject
    private HenvendelseService henvendelseService;

    @Inject
    private TilgangService tilgangService;

    @GET
    public List<Traad> hentTraader() {
        String fnr = SubjectHandler.getIdent().orElseThrow(() -> new NotAuthorizedException("Fant ikke brukers OIDC-token"));
        List<Henvendelse> henvendelser = henvendelseService.hentAlleHenvendelser(fnr);

        final Map<String, List<Henvendelse>> traader = henvendelser.stream()
                .collect(groupingBy(henvendelse -> henvendelse.traadId));

        return traader.values().stream()
                .map(this::filtrerDelsvar)
                .map(Traad::new)
                .sorted(NYESTE_FORST)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response hentEnkeltTraad(@PathParam("id") String id) {
        Optional<Traad> optionalTraad = hentTraad(id);
        if (optionalTraad.isPresent()) {
            Traad traad = optionalTraad.get();
            Traad filtrertTraad = new Traad(filtrerDelsvar(traad.meldinger));
            return ok(filtrertTraad).build();
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
        String fnr = SubjectHandler.getIdent().orElseThrow(() -> new NotAuthorizedException("Fant ikke brukers OIDC-token"));
        Henvendelse henvendelse = lagHenvendelse(fnr, sporsmal);

        Event metrikk = MetricsFactory.createEvent("mininnboks.sendsporsmal");
        metrikk.addTagToReport("tema", sporsmal.temagruppe);
        metrikk.report();

        WSSendInnHenvendelseResponse response = henvendelseService.stillSporsmal(henvendelse, fnr);

        return status(CREATED).entity(new NyHenvendelseResultat(response.getBehandlingsId())).build();
    }

    @POST
    @Path("/sporsmaldirekte")
    @Consumes(APPLICATION_JSON)
    public Response sendSporsmalDirekte(Sporsmal sporsmal) {
        String fnr = SubjectHandler.getIdent().orElseThrow(() -> new NotAuthorizedException("Fant ikke brukers OIDC-token"));
        Henvendelse henvendelse = lagHenvendelse(fnr, sporsmal);

        Event metrikk = MetricsFactory.createEvent("mininnboks.sendsporsmaldirekte");
        metrikk.addTagToReport("tema", sporsmal.temagruppe);
        metrikk.report();

        WSSendInnHenvendelseResponse response = henvendelseService.stillSporsmalDirekte(henvendelse, fnr);

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
        henvendelse.opprettet = new Date();
        henvendelse.markerSomLest();
        henvendelse.erTilknyttetAnsatt = traad.nyeste.erTilknyttetAnsatt;
        henvendelse.kontorsperreEnhet = traad.nyeste.kontorsperreEnhet;

        Event metrikk = MetricsFactory.createEvent("mininnboks.sendsvar");
        metrikk.addTagToReport("tema", traad.nyeste.temaKode);
        metrikk.report();

        String fnr = SubjectHandler.getIdent().orElseThrow(() -> new NotAuthorizedException("Fant ikke brukers OIDC-token"));
        WSSendInnHenvendelseResponse response = henvendelseService.sendSvar(henvendelse, fnr);

        return status(CREATED).entity(new NyHenvendelseResultat(response.getBehandlingsId())).build();
    }

    private Henvendelse lagHenvendelse(String fnr, Sporsmal sporsmal) {
        Temagruppe temagruppe = Temagruppe.valueOf(sporsmal.temagruppe);
        assertFritekst(sporsmal.fritekst);
        assertTemagruppeTilgang(fnr, temagruppe);

        return new Henvendelse(sporsmal.fritekst, temagruppe);
    }

    private static void assertFritekst(String fritekst) {
        if (fritekst == null) {
            throw new BadRequestException("Fritekst må være sendt med");
        } else if (fritekst.trim().length() == 0) {
            throw new BadRequestException("Fritekst må inneholde tekst");
        } else if (fritekst.trim().length() > 1000) {
            throw new BadRequestException("Fritekst kan ikke være lengre enn 1000 tegn");
        }
    }

    private void assertTemagruppeTilgang(String fnr, Temagruppe temagruppe) {
        if (!Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL.contains(temagruppe)) {
            throw new BadRequestException("Innsending på temagruppe er ikke godkjent");
        }
        if (temagruppe == Temagruppe.OKSOS && !harTilgangTilKommunalInnsending(fnr)) {
            throw new BadRequestException("Bruker har ikke lov til å sende inn henvendelse på temagruppe OKSOS.");
        }
    }

    private boolean harTilgangTilKommunalInnsending(String fnr) {
        return TilgangDTO.Resultat.OK.equals(tilgangService.harTilgangTilKommunalInnsending(fnr).getResultat());
    }

    private List<Henvendelse> filtrerDelsvar(List<Henvendelse> traad) {
        if (traadHarIkkeSkriftligSvarFraNAV(traad)) {
            return traad.stream()
                    .filter(henvendelse -> henvendelse.type != Henvendelsetype.DELVIS_SVAR_SKRIFTLIG)
                    .collect(Collectors.toList());
        }
        return traad;
    }

    private boolean traadHarIkkeSkriftligSvarFraNAV(List<Henvendelse> traad) {
        Optional<Henvendelse> skriftligSvarFraNAV = traad.stream()
                .filter(henvendelse -> henvendelse.type == Henvendelsetype.SVAR_SKRIFTLIG)
                .findAny();
        return !skriftligSvarFraNAV.isPresent();
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
