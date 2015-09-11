package no.nav.sbl.dialogarena.mininnboks.provider.rest.resources;

import no.nav.modig.content.PropertyResolver;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Path("/resources")
@Produces(APPLICATION_JSON + "; charset=UTF-8")
public class ResourcesController {

    public static final String EPOST = "bruker.epost";
    @Inject
    private PropertyResolver propertyResolver;

    @GET
    public Map<String, String> getResources(@Context HttpServletRequest request) {
        Map<String, String> resources = new HashMap<>();

        resources.putAll(propertyResolver.getAllProperties());
        resources.put("skriv.ny.link", System.getProperty("temavelger.link.url"));
        resources.put("brukerprofil.link", System.getProperty("brukerprofil.link.url"));
        resources.put("temagruppe.liste", collectionToDelimitedString(GODKJENTE_FOR_INNGAAENDE_SPORSMAAL, " "));

        return resources;
    }

}
