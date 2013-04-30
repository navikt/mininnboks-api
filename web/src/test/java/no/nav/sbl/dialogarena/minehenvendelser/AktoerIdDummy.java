package no.nav.sbl.dialogarena.minehenvendelser;


public class AktoerIdDummy implements AktoerIdService {

    private String aktoerId;

    @Override
    public String getAktoerId() {
        return aktoerId == null ? "svein" : aktoerId;
    }

    public void setAktoerId(String aktoerId) {
        this.aktoerId = aktoerId;
    }

}
