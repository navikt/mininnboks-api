package no.nav.sbl.dialogarena.mininnboks.provider.rest;

import no.nav.sbl.dialogarena.mininnboks.config.HenvendelseServiceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@EnableWebMvc
@Import(HenvendelseServiceConfig.class)
@ComponentScan(basePackageClasses = RestConfig.class)
public class RestConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(asList(APPLICATION_JSON));
        converters.add(jsonConverter);
    }
}
