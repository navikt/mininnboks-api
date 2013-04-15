package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import org.springframework.core.env.Environment;

public class PropertyFileContentRetriver {

    private Environment env;

    public void setEnviroment(Environment env) {
        this.env = env;
    }

    public String hentTekst(String key, String locale) {
        String tekst = env.getProperty(key);
        return tekst;
    }

}
