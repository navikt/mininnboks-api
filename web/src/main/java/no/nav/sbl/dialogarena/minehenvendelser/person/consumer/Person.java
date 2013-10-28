package no.nav.sbl.dialogarena.minehenvendelser.person.consumer;

import no.nav.sbl.dialogarena.minehenvendelser.person.kontaktdetaljer.Preferanser;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker;

import java.io.Serializable;

public class Person implements Serializable {
    private Preferanser preferanser = new Preferanser();
    private XMLBruker personFraTPS;

    public Person() {
    }

    public Preferanser getPreferanser() {
        return preferanser;
    }

    public void setPreferanser(Preferanser preferanser) {
        this.preferanser = preferanser;
    }

    public void setPersonFraTPS(XMLBruker personFraTPS) {
        this.personFraTPS = personFraTPS;
    }

    public XMLBruker getPersonFraTPS() {
        return personFraTPS;
    }
}