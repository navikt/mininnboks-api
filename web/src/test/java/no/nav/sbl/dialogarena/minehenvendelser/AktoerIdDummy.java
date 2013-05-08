package no.nav.sbl.dialogarena.minehenvendelser;


import no.nav.modig.core.context.Principal;

import static no.nav.modig.core.context.SecurityContext.getCurrent;

public class AktoerIdDummy implements AktoerIdService {

    @Override
    public String getAktoerId() {
       return getCurrent().getPrincipal().getUserId();
    }

    public void setAktoerId(String aktoerId) {
        getCurrent().removeAll();
        Principal principal = new Principal.Builder()
                .userId(aktoerId)
                .identType("EksternBruker")
                .authenticationLevel("4")
                .consumerId("minehenvendelser")
                .build();
        getCurrent().setPrincipal(principal);
    }

}
