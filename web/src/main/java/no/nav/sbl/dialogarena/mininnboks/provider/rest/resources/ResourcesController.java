package no.nav.sbl.dialogarena.mininnboks.provider.rest.resources;

import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.mininnboks.consumer.EpostService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("/resources")
@Produces(APPLICATION_JSON+"; charset=UTF-8")
public class ResourcesController {

    public static final String EPOST = "bruker.epost";
    @Inject
    private EpostService epostService;
    @Inject
    private PropertyResolver propertyResolver;

    @GET
    public Map<String, String> getResources(@Context HttpServletRequest request) {
        Map<String, String> resources = new HashMap<>();

        resources.putAll(propertyResolver.getAllProperties());
        resources.put("skriv.ny.link", System.getProperty("temavelger.link.url"));
        resources.put("brukerprofil.link", System.getProperty("brukerprofil.link.url"));
        try {
            resources.put("bruker.epost", epost(request));
        } catch (Exception ignored) {}

        return resources;
    }

    private String epost(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String epost = (String) session.getAttribute(EPOST);

        if (!isBlank(epost)) {
            return epost;
        }

        epost = epostService.hentEpostadresse();
        session.setAttribute(EPOST, epost);

        return epost;

    }

}
