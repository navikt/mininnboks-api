package no.nav.sbl.dialogarena.mininnboks.provider.rest.resources;

import no.nav.modig.content.PropertyResolver;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/resources")
@Produces(APPLICATION_JSON)
public class ResourcesController {

    @Inject
    private PropertyResolver propertyResolver;
    @Inject
    @Named("keys")
    private List<String> keys;

    @GET
    public Map<String, String> getResources() {
        Map<String, String> resources = new HashMap<>();
        resources.put("skriv.ny.link", System.getProperty("temavelger.link.url"));
        for (String key : keys) {
            resources.put(key, propertyResolver.getProperty(key));
        }
        return resources;
    }

}
