package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPreferanser;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLSpraak;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HentBrukerProfilConsumerTest {

    private static final LocalDate IDAG = new LocalDate(2013, 5, 4);
    private static final String IDENT = "123456***REMOVED***";
    private HentBrukerProfilConsumer consumer;
    private XMLBruker response;

    @Mock
    private BrukerprofilPortType brukerprofilServiceMock;


    @Before
    public void initIntegrationServiceWithMock() throws Exception {
        consumer = new HentBrukerProfilConsumer(brukerprofilServiceMock);
        response = (XMLBruker) stubResponseFromService().getPerson();
    }

    @Test
    public void skalHentePerson() {
        Person person = consumer.hentPerson(IDENT);
        assertNotNull(person);
    }

    @Test
    public void preferanser() {
        response.withPreferanser(
                new XMLPreferanser().withElektroniskKorrespondanse(true)
                        .withMaalform(new XMLSpraak().withKodeverksRef("kodeverksRef"))
        );

        Person person = consumer.hentPerson(IDENT);
        assertThat(person.getPreferanser().isElektroniskSamtykke(), is(true));
    }

    @Test(expected = ApplicationException.class)
    @SuppressWarnings("unchecked")
    public void kasterApplicationExceptionVedSikkerhetsbegrensningsfeil() throws Exception {
        when(brukerprofilServiceMock.hentKontaktinformasjonOgPreferanser(any(XMLHentKontaktinformasjonOgPreferanserRequest.class)))
                .thenThrow(HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning.class);
        consumer.hentPerson(IDENT);
    }

    @Test(expected = ApplicationException.class)
    @SuppressWarnings("unchecked")
    public void kasterApplicationExceptionVedPersonIkkeFunnet() throws Exception {
        when(brukerprofilServiceMock.hentKontaktinformasjonOgPreferanser(any(XMLHentKontaktinformasjonOgPreferanserRequest.class)))
                .thenThrow(HentKontaktinformasjonOgPreferanserPersonIkkeFunnet.class);
        consumer.hentPerson(IDENT);
    }

    private XMLHentKontaktinformasjonOgPreferanserResponse stubResponseFromService() throws Exception {
        XMLHentKontaktinformasjonOgPreferanserResponse xmlResponse = new XMLHentKontaktinformasjonOgPreferanserResponse();
        xmlResponse.withPerson(new XMLBruker()
                .withPreferanser(new XMLPreferanser().withMaalform(new XMLSpraak())));
        when(brukerprofilServiceMock.hentKontaktinformasjonOgPreferanser(any(XMLHentKontaktinformasjonOgPreferanserRequest.class))).thenReturn(xmlResponse);
        return xmlResponse;
    }
}