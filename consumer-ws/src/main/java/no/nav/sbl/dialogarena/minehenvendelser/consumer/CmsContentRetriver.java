package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.modig.content.ValueRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.ParagraphRemover;

public class CmsContentRetriver {

    private String cmsIp;
    private ValueRetriever siteContentRetriver;
    private String defaultLocale;
    

    public String getCmsIp() {
        return cmsIp;
    }

    public void setCmsIp(String cmsIp) {
        this.cmsIp = cmsIp;
    }

    public void setInnholdstekster(ValueRetriever siteContentRetriever) {
        this.siteContentRetriver =  siteContentRetriever;
    }
    
    public void setDefaultLocale(String locale){
        this.defaultLocale = locale;
    }
    
    public String hentTekst(String key){
        return hentTekst(key,defaultLocale);
    }
    
    public String hentTekst(String key, String locale){
        return ParagraphRemover.remove(siteContentRetriver.getValueOf(key, locale));
    }
    
    public String hentArtikkel(String key){
        return hentArtikkel(key,defaultLocale);
    }

    public String hentArtikkel(String key, String locale){
        return ParagraphRemover.remove(siteContentRetriver.getValueOf(key, locale));
    }
}
