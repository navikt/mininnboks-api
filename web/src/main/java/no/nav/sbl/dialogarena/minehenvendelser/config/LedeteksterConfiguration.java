package no.nav.sbl.dialogarena.minehenvendelser.config;

import javax.inject.Inject;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.PropertyFileContentRetriver;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:ledetekster.properties")
public class LedeteksterConfiguration {

    @Inject
    private Environment env;
    
    public PropertyFileContentRetriver propertyFileContentRetriver(){
        PropertyFileContentRetriver contentRetriver = new PropertyFileContentRetriver();
        contentRetriver.setEnviroment(env);
        return contentRetriver;
    }
}
