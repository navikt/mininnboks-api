package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.core.context.Principal;

import static no.nav.modig.core.context.SecurityContext.getCurrent;

/**
 * Implementasjon som henter AktørID fra sikkerhetskontekst.
 */
public class AktoerIdSecurityContext implements AktoerIdService {

    private static final String APPLICATION_ID = "BD04";

    @Override
    public String getAktoerId() {
        return getCurrent().getPrincipal().getUserId();
    }

    @Override
    public void setAktoerId(String aktoerId) {
        getCurrent().removeAll();
        Principal principal = new Principal.Builder()
                .userId(aktoerId)
                .identType("EksternBruker")
                .authenticationLevel("4")
                .consumerId(APPLICATION_ID)
                .build();
        getCurrent().setPrincipal(principal);
    }
}
