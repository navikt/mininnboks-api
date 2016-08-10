package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.DOKUMENT_VARSEL;
import static no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel.SporsmalVarselUtils.erUbesvart;
import static no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel.SporsmalVarselUtils.erUlest;

public class SporsmalVarsel {

    public static enum Status {ULEST, UBESVART}

    public String behandlingskjedeId;
    public Date opprettetDato;
    public Henvendelsetype type;
    public String uri;
    public List<Status> statuser = new ArrayList<>();

    public SporsmalVarsel(Henvendelse henvendelse) {
        this.behandlingskjedeId = henvendelse.traadId;
        this.opprettetDato = henvendelse.opprettet.toDate();
        this.type = henvendelse.type;
        this.uri = lagDirektelenkeTilMelding(henvendelse);

        if (erUbesvart(henvendelse)) {
            this.statuser.add(Status.UBESVART);
        }
        if (erUlest(henvendelse)) {
            this.statuser.add(Status.ULEST);
        }
    }

    private String lagDirektelenkeTilMelding(Henvendelse henvendelse) {
        if (DOKUMENT_VARSEL == this.type) {
            return String.format("%s/?varselId=%s", getProperty("mininnboks.link.url"), henvendelse.korrelasjonsId);
        }
        return String.format("%s/traad/%s", getProperty("mininnboks.link.url"), henvendelse.traadId);
    }
}
