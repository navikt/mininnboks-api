package no.nav.sbl.dialogarena.minehenvendelser;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

/**
 * Implementasjon som henter AktørID fra sikkerhetskontekst.
 */
public class FoedselsnummerService {

    public String getFoedselsnummer() {
        return getSubjectHandler().getUid();
    }

}
