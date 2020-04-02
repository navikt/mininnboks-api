package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.common.auth.SubjectHandler;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSAnsvarligEnhet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserResponse;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.sbl.dialogarena.mininnboks.TestUtils.MOCK_SUBJECT;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPersonServiceTest {
    @Mock
    private BrukerprofilV3 brukerprofilV3;
    @Mock
    private PersonV3 personV3;
    private PersonService.Default personService;

    @Before
    public void setUp() {
        personService = new PersonService.Default(brukerprofilV3, personV3);
    }

    @Test
    public void henterEnhet() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt {
        String enhet = "1234";
        when(brukerprofilV3.hentKontaktinformasjonOgPreferanser(any(WSHentKontaktinformasjonOgPreferanserRequest.class)))
                .thenReturn(new WSHentKontaktinformasjonOgPreferanserResponse().withBruker(new WSBruker().withAnsvarligEnhet(new WSAnsvarligEnhet().withOrganisasjonselementId(enhet))));

        SubjectHandler.withSubject(MOCK_SUBJECT, () -> {
            assertThat(personService.hentEnhet().get(), is(enhet));
        });
    }

    @Test(expected = RuntimeException.class)
    public void kasterRuntimeExceptionOmEnhetIkkeKanhentes() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {

        when(brukerprofilV3.hentKontaktinformasjonOgPreferanser(any(WSHentKontaktinformasjonOgPreferanserRequest.class))).thenThrow(new HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning());

        personService.hentEnhet();

    }

}
