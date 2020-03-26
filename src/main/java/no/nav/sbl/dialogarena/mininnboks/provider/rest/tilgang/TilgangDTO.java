package no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang;

public class TilgangDTO {
    public final Resultat resultat;
    public final String melding;

    public TilgangDTO(Resultat resultat, String melding) {
        this.resultat = resultat;
        this.melding = melding;
    }

    enum  Resultat {
        FEILET, KODE6, INGEN_ENHET, OK
    }
}
