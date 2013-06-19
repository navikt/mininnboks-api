package no.nav.sbl.dialogarena.minehenvendelser;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class AktoerIdDummy implements AktoerIdService {

    @Override
    public String getAktoerId() {
       return getSubjectHandler().getUid();
    }

}
