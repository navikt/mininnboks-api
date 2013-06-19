package no.nav.sbl.dialogarena.minehenvendelser;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

/**
 * Implementasjon som henter AktørID fra sikkerhetskontekst.
 */
public class AktoerIdSecurityContext implements AktoerIdService {

    @Override
    public String getAktoerId() {
        return getSubjectHandler().getUid();
    }

}
