package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

public enum Tema {
    ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT("Arbeidssøker, arbeidsavklaring, sykemeldt"),
    FAMILIE_OG_BARN("Familie og barn"),
    HJELPEMIDLER("Hjelpemidler"),
    INTERNASJONALT("Internasjonalt"),
    PENSJON("Pensjon"),
    SOSIALE_TJENESTER("Sosiale tjenester"),
    UFOREPENSJON("Uførepensjon"),
    OVRIGE_HENVENDELSER("Øvrige henvendelser");

    private String temanavn;
    private Tema(String temanavn) {
        this.temanavn = temanavn;
    }

    @Override
    public String toString() {
        return this.temanavn;
    }
}
