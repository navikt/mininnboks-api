package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;

import java.util.Date;

public class SporsmalVarsel {
    public String traadId;
    public Date opprettetDato;
    public Henvendelsetype type;

    public SporsmalVarsel(String traadId, Date opprettetDato, Henvendelsetype type) {
        this.traadId = traadId;
        this.opprettetDato = opprettetDato;
        this.type = type;
    }
}
