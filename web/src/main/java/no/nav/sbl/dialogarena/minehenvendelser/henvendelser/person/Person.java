package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.kontaktdetaljer.Preferanser;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;

import java.io.Serializable;

public class Person implements Serializable {
    private Preferanser preferanser = new Preferanser();
    private XMLHentKontaktinformasjonOgPreferanserResponse responseFraTPS;

    public Person() {
    }

    public Preferanser getPreferanser() {
        return preferanser;
    }

    public void setPreferanser(Preferanser preferanser) {
        this.preferanser = preferanser;
    }

    public void setResponseFraTPS(XMLHentKontaktinformasjonOgPreferanserResponse responseFraTPS) {
        this.responseFraTPS = responseFraTPS;
    }

    public XMLHentKontaktinformasjonOgPreferanserResponse getResponseFraTPS() {
        return responseFraTPS;
    }
}