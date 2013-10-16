package no.nav.sbl.dialogarena.minehenvendelser.henvendelser;

import no.nav.modig.content.CmsContentRetriever;
import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;

import java.util.Locale;
import java.util.MissingResourceException;

public class EnonicResourceLoader implements IStringResourceLoader {

    private CmsContentRetriever cmsContentRetriever;

    public EnonicResourceLoader(CmsContentRetriever cmsContentRetriever) {
        this.cmsContentRetriever = cmsContentRetriever;
    }

    @Override
    public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
        return getFromEnonic(key);
    }

    @Override
    public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
        return getFromEnonic(key);
    }

    private String getFromEnonic(String key) {
        System.out.println("\n\n\n\n\nHenter tekst med key " + key + " fra CMS \n\n\n\n\n");
        try {
            return cmsContentRetriever.hentTekst(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }
}
