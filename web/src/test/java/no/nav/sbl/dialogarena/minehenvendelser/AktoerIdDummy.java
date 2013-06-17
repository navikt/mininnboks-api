package no.nav.sbl.dialogarena.minehenvendelser;


import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.domain.IdentType;

import static no.nav.modig.core.context.SubjectHandlerUtils.SubjectBuilder;
import static no.nav.modig.core.context.SubjectHandlerUtils.setSubject;


public class AktoerIdDummy implements AktoerIdService {

    @Override
    public String getAktoerId() {
       return SubjectHandler.getSubjectHandler().getUid();
    }

    public void setAktoerId(String aktoerId) {
        setSubject(new SubjectBuilder(aktoerId, IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }

}
