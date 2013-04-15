package no.nav.sbl.dialogarena.minehenvendelser.consumer;

public class Innholdstekster {

    private CmsContentRetriver cmsContentRetriver;
    private PropertyFileContentRetriver propertyFileContentRetriver;
    private String defaultLocale;

    public String hentTekst(String key) {
        return hentTekst(key, defaultLocale);
    }

    public String hentTekst(String key, String locale) {
        String tekst = propertyFileContentRetriver.hentTekst(key, locale);
        if (tekst != null) {
            return tekst;
        }
        String cmsTekst = cmsContentRetriver.hentTekst(key, locale);
        if (cmsTekst != null) {
            return cmsTekst;
        }
        return null;
    }

    public String hentArtikkel(String key) {
        return hentArtikkel(key, defaultLocale);
    }

    public String hentArtikkel(String key, String locale) {
        return hentTekst(key, locale);
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void setCmsContentRetriver(CmsContentRetriver cmsContentRetriver) {
        this.cmsContentRetriver = cmsContentRetriver;
    }

    public void setPropertyFileContentRetriver(PropertyFileContentRetriver propertyFileContentRetriver) {
        this.propertyFileContentRetriver = propertyFileContentRetriver;
    }

}
