package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.DOKUMENT_VARSEL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.OPPGAVE_VARSEL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;

public class UbehandletMelding {

    public enum Status {ULEST, UBESVART}

    public static Predicate<Henvendelse> erUbesvart = (henvendelse) -> henvendelse.type == SPORSMAL_MODIA_UTGAAENDE;
    public static Predicate<Henvendelse> erUlest = (henvendelse) -> !henvendelse.isLest();

    public String behandlingskjedeId;
    public Date opprettetDato;
    public Henvendelsetype type;
    public String undertype;
    public String uri;
    public List<Status> statuser = new ArrayList<>();
    public String varselid;

    public UbehandletMelding(Henvendelse henvendelse) {
        this.behandlingskjedeId = henvendelse.traadId;
        this.opprettetDato = henvendelse.opprettet.toDate();
        this.type = henvendelse.type;
        this.undertype = henvendelse.oppgaveType;
        this.uri = lagDirektelenkeTilMelding(henvendelse);
        this.varselid = henvendelse.korrelasjonsId;

        if (erUbesvart.test(henvendelse)) {
            this.statuser.add(Status.UBESVART);
        }
        if (erUlest.test(henvendelse)) {
            this.statuser.add(Status.ULEST);
        }
    }

    private String lagDirektelenkeTilMelding(Henvendelse henvendelse) {
        if (DOKUMENT_VARSEL == this.type || OPPGAVE_VARSEL == this.type) {
            return String.format("%s/?varselid=%s", getProperty("mininnboks.link.url"), henvendelse.korrelasjonsId);
        }
        return String.format("%s/traad/%s", getProperty("mininnboks.link.url"), henvendelse.traadId);
    }
}
