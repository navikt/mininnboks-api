package no.nav.sbl.dialogarena.minehenvendelser.consumer.util;

import no.nav.modig.content.ValueRetriever;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.ParagraphRemover.remove;

/**
 * Utilklasse for interaksjon med CMS
 */
public class CmsContentRetriever {

    private String cmsIp;
    private ValueRetriever siteTextRetriever;
    private ValueRetriever siteArticleRetriever;
    private String defaultLocale;

    public String getCmsIp() {
        return cmsIp;
    }

    public void setCmsIp(String cmsIp) {
        this.cmsIp = cmsIp;
    }

    public void setTeksterRetriever(ValueRetriever siteContentRetriever) {
        this.siteTextRetriever = siteContentRetriever;
    }

    public void setArtikkelRetriever(ValueRetriever siteArticleRetriever) {
        this.siteArticleRetriever = siteArticleRetriever;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String hentTekst(String key){
        return hentTekst(key,defaultLocale);
    }

    public String hentTekst(String key, String locale) {
        return remove(siteTextRetriever.getValueOf(key, locale));
    }

    public String hentArtikkel(String key){
        return hentArtikkel(key, defaultLocale);
    }

    public String hentArtikkel(String key, String locale) {
        return remove(siteArticleRetriever.getValueOf(key, locale));
    }
}
