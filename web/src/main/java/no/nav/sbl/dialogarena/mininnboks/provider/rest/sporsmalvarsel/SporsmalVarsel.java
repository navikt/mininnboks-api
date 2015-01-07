package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;

import java.util.Date;

import static no.nav.sbl.dialogarena.mininnboks.WicketApplication.INNBOKS_PATH;
import static no.nav.sbl.dialogarena.mininnboks.innboks.Innboks.TRAAD_ID_PARAMETER_NAME;

public class SporsmalVarsel {
    public String traadId;
    public Date opprettetDato;
    public Henvendelsetype type;
    public String uri;

    public SporsmalVarsel(String traadId, Date opprettetDato, Henvendelsetype type) {
        this.traadId = traadId;
        this.opprettetDato = new Date(opprettetDato.getTime());
        this.type = type;
        this.uri = lagDirektelenkeTilTraad(traadId);
    }

    private String lagDirektelenkeTilTraad(String traadId) {
        return System.getProperty("mininnboks.link.url") + "/" + INNBOKS_PATH + "?" + TRAAD_ID_PARAMETER_NAME + "=" + traadId;
    }
}
