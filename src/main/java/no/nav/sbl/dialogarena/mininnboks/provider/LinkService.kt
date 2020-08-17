package no.nav.sbl.dialogarena.mininnboks.provider;

import lombok.extern.slf4j.Slf4j;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.util.EnvironmentUtils;

import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.DOKUMENT_VARSEL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.OPPGAVE_VARSEL;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Slf4j
public class LinkService {

    public static final String MININNBOKS_LINK_PROPERTY = "MININNBOKS_LINK_URL";
    private static final String MININNBOKS_LINK = getRequiredProperty(MININNBOKS_LINK_PROPERTY);

    public static final String TEMAVELGER_LINK_PROPERTY = "TEMAVELGER_LINK_URL";
    public static final String TEMAVELGER_LINK = EnvironmentUtils.getRequiredProperty(TEMAVELGER_LINK_PROPERTY);
    public static final String BRUKERPROFIL_LINK_PROPERTY = "BRUKERPROFIL_LINK_URL";
    public static final String BRUKERPROFIL_LINK = EnvironmentUtils.getRequiredProperty(BRUKERPROFIL_LINK_PROPERTY);
    public static final String SAKSOVERSIKT_LINK_PROPERTY = "SAKSOVERSIKT_LINK_URL";
    public static final String SAKSOVERSIKT_LINK = EnvironmentUtils.getRequiredProperty(SAKSOVERSIKT_LINK_PROPERTY);

    public static String lagDirektelenkeTilMelding(Henvendelse henvendelse) {
        if (DOKUMENT_VARSEL == henvendelse.type || OPPGAVE_VARSEL == henvendelse.type) {
            return String.format("%s/?varselid=%s", MININNBOKS_LINK, henvendelse.korrelasjonsId);
        }
        return String.format("%s/traad/%s", MININNBOKS_LINK, henvendelse.traadId);
    }

    public static void touch() {
        log.info(MININNBOKS_LINK);
        log.info(TEMAVELGER_LINK);
        log.info(BRUKERPROFIL_LINK);
        log.info(SAKSOVERSIKT_LINK);
    }

}
