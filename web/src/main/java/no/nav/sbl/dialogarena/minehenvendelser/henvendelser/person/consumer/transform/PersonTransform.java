package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.kontaktdetaljer.Preferanser;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPreferanser;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;

public class PersonTransform {

    public Person mapToPerson(XMLHentKontaktinformasjonOgPreferanserResponse response) {
        if (response == null) {
            return new Person();
        }

        return setPreferanser(response);
    }

    private Person setPreferanser(XMLHentKontaktinformasjonOgPreferanserResponse response) {
        Person person = new Person();
        XMLBruker soapBruker = (XMLBruker) response.getPerson();
        XMLPreferanser soapBrukerPreferanser = soapBruker.getPreferanser();
        Preferanser preferanser = new Preferanser();

        preferanser.setElektroniskSamtykke(soapBrukerPreferanser.isElektroniskKorrespondanse());
        preferanser.getMaalform().setKodeverkRef(soapBrukerPreferanser.getMaalform().getKodeverksRef());
        person.setPreferanser(preferanser);

        return person;
    }
}