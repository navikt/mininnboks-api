package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.kontaktdetaljer.Preferanser;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPreferanser;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;

public class PersonTransform {

    public Person mapToPerson(XMLHentKontaktinformasjonOgPreferanserResponse response) {
        if (response == null) {
            return new Person();
        }

        Person person = new Person();
        person.setPersonFraTPS((XMLBruker) response.getPerson());
        return setPreferanser(person);
    }

    private Person setPreferanser(Person person) {
        XMLBruker soapBruker = person.getPersonFraTPS();
        XMLPreferanser soapBrukerPreferanser = soapBruker.getPreferanser();
        Preferanser preferanser = new Preferanser();

        preferanser.setElektroniskSamtykke(soapBrukerPreferanser.isElektroniskKorrespondanse());
        preferanser.getMaalform().setValue(soapBrukerPreferanser.getMaalform().getValue());
        person.setPreferanser(preferanser);

        return person;
    }
}