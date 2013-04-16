package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.modig.content.ValueRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.ParagraphRemover;

public class CmsContentRetriver {

    private String cmsIp;
    private ValueRetriever siteTextRetriver;
    private ValueRetriever siteArticleRetriver;
    private String defaultLocale;

    public String getCmsIp() {
        return cmsIp;
    }

    public void setCmsIp(String cmsIp) {
        this.cmsIp = cmsIp;
    }

    public void setTeksterRetriver(ValueRetriever siteContentRetriever) {
        this.siteTextRetriver = siteContentRetriever;
    }

    public void setArtikkelRetriver(ValueRetriever siteArticleRetriver) {
        this.siteArticleRetriver = siteArticleRetriver;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String hentTekst(String key){
        return hentTekst(key,defaultLocale);
    }

    public String hentTekst(String key, String locale) {
        return ParagraphRemover.remove(siteTextRetriver.getValueOf(key, locale));
    }

    public String hentArtikkel(String key){
        return hentArtikkel(key, defaultLocale);
    }

    public String hentArtikkel(String key, String locale) {
        return ParagraphRemover.remove(siteArticleRetriver.getValueOf(key, locale));
    }
}
