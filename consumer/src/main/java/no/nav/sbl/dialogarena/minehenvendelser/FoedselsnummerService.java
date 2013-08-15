package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.core.context.SubjectHandler;

/**
 * Implementasjon som henter AktørID fra sikkerhetskontekst.
 */
public class FoedselsnummerService {

    public String getFoedselsnummer() {
        return SubjectHandler.getSubjectHandler().getUid();
    }

}
