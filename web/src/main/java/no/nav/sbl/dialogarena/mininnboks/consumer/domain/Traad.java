package no.nav.sbl.dialogarena.mininnboks.consumer.domain;

public class Traad {
    public final Integer antallHenvendelser;
    public final Henvendelse nyesteHenvendelse;

    public Traad(Integer antallHenvendelser, Henvendelse nyesteHenvendelse) {
        this.antallHenvendelser = antallHenvendelser;
        this.nyesteHenvendelse = nyesteHenvendelse;
    }
}
