package no.nav.sbl.dialogarena.minehenvendelser;

/**
 * Implementasjon som henter AktørID fra sikkerhetskontekst.
 */
public class AktoerIdSecurityContext implements AktoerIdService {

    private String aktoerId;

    @Override
    public String getAktoerId() {
        return aktoerId == null ? "***REMOVED***" : aktoerId;
    }

    @Override
    public void setAktoerId(String aktoerId) {
        this.aktoerId = aktoerId;
    }
}
