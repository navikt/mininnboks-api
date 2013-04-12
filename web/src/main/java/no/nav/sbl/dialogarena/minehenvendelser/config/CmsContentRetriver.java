package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.content.ValueRetriever;

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
    
    public String hentTekst(String key, String locale){
        return siteContentRetriver.getValueOf(key, locale);
    }
    
    public String hentTekst(String key){
        return hentTekst(key,defaultLocale);
    }
}
