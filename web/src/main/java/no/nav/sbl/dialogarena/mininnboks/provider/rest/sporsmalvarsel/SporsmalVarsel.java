package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static no.nav.sbl.dialogarena.mininnboks.WicketApplication.INNBOKS_PATH;
import static no.nav.sbl.dialogarena.mininnboks.innboks.Innboks.TRAAD_ID_PARAMETER_NAME;
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
        this.uri = lagDirektelenkeTilTraad(henvendelse.traadId);

        if (erUbesvart(henvendelse)) {
            this.statuser.add(Status.UBESVART);
        }
        if (erUlest(henvendelse)) {
            this.statuser.add(Status.ULEST);
        }
    }

    private String lagDirektelenkeTilTraad(String traadId) {
        return System.getProperty("mininnboks.link.url") + "/" + INNBOKS_PATH + "?" + TRAAD_ID_PARAMETER_NAME + "=" + traadId;
    }
}
