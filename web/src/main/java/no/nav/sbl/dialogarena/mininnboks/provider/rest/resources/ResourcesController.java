package no.nav.sbl.dialogarena.mininnboks.provider.rest.resources;

import no.nav.sbl.dialogarena.mininnboks.message.NavMessageSource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Locale;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Path("/resources")
@Produces(APPLICATION_JSON + "; charset=UTF-8")
public class ResourcesController {

    @Inject
    private NavMessageSource messageSource;

    @GET
    public Properties hentTekster(@QueryParam("type") String type) {
        Properties norske = messageSource.getBundleFor(type, new Locale("nb", "NO"));

        norske.put("skriv.ny.link", System.getProperty("temavelger.link.url"));
        norske.put("brukerprofil.link", System.getProperty("brukerprofil.link.url"));
        norske.put("temagruppe.liste", collectionToDelimitedString(GODKJENTE_FOR_INNGAAENDE_SPORSMAAL, " "));

        return norske;
    }

}
