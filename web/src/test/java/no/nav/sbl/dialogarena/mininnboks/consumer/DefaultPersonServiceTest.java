package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.BrukerprofilV2;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.informasjon.WSAnsvarligEnhet;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.meldinger.WSHentKontaktinformasjonOgPreferanserResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPersonServiceTest {

    @Mock
    private BrukerprofilV2 brukerprofilV2;
    private PersonService.Default personService;

    @Before
    public void setUp() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());

        personService = new PersonService.Default(brukerprofilV2);
    }

    @Test
    public void henterEnhet() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        String enhet = "1234";
        when(brukerprofilV2.hentKontaktinformasjonOgPreferanser(any(WSHentKontaktinformasjonOgPreferanserRequest.class)))
                .thenReturn(new WSHentKontaktinformasjonOgPreferanserResponse().withPerson(new WSBruker().withAnsvarligEnhet(new WSAnsvarligEnhet().withOrganisasjonselementID(enhet))));

        assertThat(personService.hentEnhet().get(), is(enhet));
    }

}