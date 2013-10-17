package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer;


import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform.PersonTransform;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;


public class HentBrukerprofilPerson {

    private final BrukerprofilPortType webservice;

    public HentBrukerprofilPerson(BrukerprofilPortType brukerprofilPortType) {
        webservice = brukerprofilPortType;
    }

    public Person hentPerson(String ident) {
        XMLHentKontaktinformasjonOgPreferanserResponse response;
        try {
            response = webservice.hentKontaktinformasjonOgPreferanser(new XMLHentKontaktinformasjonOgPreferanserRequest().withIdent(ident));
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning | HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            throw new ApplicationException("Person med id '" + ident + "': " + e.getMessage(), e);
        }

        return new PersonTransform().mapToPerson(response);
    }
}