package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.core.context.SubjectHandler;

/**
 * Implementasjon som henter AktørID fra sikkerhetskontekst.
 */
public class AktoerIdSecurityContext implements AktoerIdService {

    @Override
    public String getAktoerId() {
        return SubjectHandler.getSubjectHandler().getUid();
    }

    @Override
    public void setAktoerId(String aktoerId) {
        //Do nothing
    }

}
