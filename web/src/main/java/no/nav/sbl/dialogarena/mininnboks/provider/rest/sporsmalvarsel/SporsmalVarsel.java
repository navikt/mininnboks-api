package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;

public class SporsmalVarsel {

    public enum Status {ULEST, UBESVART}

    public static Predicate<Henvendelse> erUbesvart = (henvendelse) -> henvendelse.type == SPORSMAL_MODIA_UTGAAENDE;
    public static Predicate<Henvendelse> erUlest = (henvendelse) -> !henvendelse.isLest();

    public String behandlingskjedeId;
    public Date opprettetDato;
    public Henvendelsetype type;
    public String uri;
    public List<Status> statuser = new ArrayList<>();

    public SporsmalVarsel(Henvendelse henvendelse) {
        this.behandlingskjedeId = henvendelse.traadId;
        this.opprettetDato = henvendelse.opprettet.toDate();
        this.type = henvendelse.type;
        this.uri = lagDirektelenkeTilTraad(henvendelse.traadId);

        if (erUbesvart.test(henvendelse)) {
            this.statuser.add(Status.UBESVART);
        }
        if (erUlest.test(henvendelse)) {
            this.statuser.add(Status.ULEST);
        }
    }

    private String lagDirektelenkeTilTraad(String traadId) {
        return String.format("%s/traad/%s", getProperty("mininnboks.link.url"), traadId);
    }
}
