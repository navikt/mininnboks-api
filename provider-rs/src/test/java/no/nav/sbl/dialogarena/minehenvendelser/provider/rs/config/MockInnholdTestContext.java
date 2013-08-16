package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class MockInnholdTestContext {

    @Bean
    public Kodeverk kodeverk() {
        return new Kodeverk() {
            @Override
            public String getKode(String vedleggsIdOrSkjemaId, Nokkel nokkel) {
                return null;
            }

            @Override
            public String getTittel(String vedleggsIdOrSkjemaId) {
                return "Mocked title 1";
            }

            @Override
            public Map<Nokkel, String> getKoder(String vedleggsIdOrskjemaId) {
                return null;
            }

            @Override
            public boolean isEgendefinert(String vedleggsIdOrskjemaId) {
                return false;
            }
        };
    }
}
