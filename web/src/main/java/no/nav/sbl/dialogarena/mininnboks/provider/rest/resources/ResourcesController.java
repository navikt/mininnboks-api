package no.nav.sbl.dialogarena.mininnboks.provider.rest.resources;

import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Path("/resources")
@Produces(APPLICATION_JSON + "; charset=UTF-8")
public class ResourcesController {

    public static final String NORSK = "nb";

    @Inject
    TekstService tekstService;

    @GET
    public Map<String, Object> hentTekster() {
        Map<String, Object> tekster = new HashMap<>();
        tekster.putAll(tekstService.hentTekster(NORSK));
        tekster.put("skriv.ny.link", System.getProperty("temavelger.link.url"));
        tekster.put("brukerprofil.link", System.getProperty("brukerprofil.link.url"));
        tekster.put("saksoversikt.link", System.getProperty("saksoversikt.link.url"));
        tekster.put("temagruppe.liste", collectionToDelimitedString(GODKJENTE_FOR_INNGAAENDE_SPORSMAAL, " "));

        return tekster;
    }

}
