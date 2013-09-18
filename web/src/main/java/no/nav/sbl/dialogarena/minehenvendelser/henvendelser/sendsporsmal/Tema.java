package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

public enum Tema {
    UFORE("Uføre"), SYKEPENGER("Sykepenger"), PENSJON("Pensjon"), ANNET("Annet");

    private String navn;

    private Tema(String navn) {
        this.navn = navn;
    }

    public String navn() {
        return navn;
    }
}
