package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLEPost;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLElektroniskKommunikasjonskanal;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLTelefonnummer;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;
import no.nav.tjeneste.virksomhet.person.v2.PersonV2;
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

    private static final String EPOSTADRESSE = "epost@example.com";
    @Mock
    private BrukerprofilPortType brukerprofilPortType;
    @Mock
    private PersonV2 personV2;
    private PersonService.Default personService;

    @Before
    public void setUp() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());

        personService = new PersonService.Default(brukerprofilPortType, personV2);
    }

    @Test
    public void henterUtEpostadressenFraResponse() throws Exception {
        when(brukerprofilPortType.hentKontaktinformasjonOgPreferanser(any(XMLHentKontaktinformasjonOgPreferanserRequest.class))).thenReturn(
                new XMLHentKontaktinformasjonOgPreferanserResponse().withPerson(
                        new XMLBruker().withElektroniskKommunikasjonskanal(
                                new XMLElektroniskKommunikasjonskanal()
                                        .withElektroniskAdresse(new XMLEPost().withIdentifikator(EPOSTADRESSE))
                        )
                )
        );
        assertThat(personService.hentEpostadresse(), is(EPOSTADRESSE));
    }

    @Test
    public void girTomStringHvisDetIkkeFinnesEpostadresse() throws Exception {
        when(brukerprofilPortType.hentKontaktinformasjonOgPreferanser(any(XMLHentKontaktinformasjonOgPreferanserRequest.class))).thenReturn(
                new XMLHentKontaktinformasjonOgPreferanserResponse().withPerson(
                        new XMLBruker().withElektroniskKommunikasjonskanal(
                                new XMLElektroniskKommunikasjonskanal()
                                        .withElektroniskAdresse(new XMLTelefonnummer().withIdentifikator("555-12345"))
                        )
                )
        );
        assertThat(personService.hentEpostadresse(), is(""));

        when(brukerprofilPortType.hentKontaktinformasjonOgPreferanser(any(XMLHentKontaktinformasjonOgPreferanserRequest.class))).thenReturn(
                new XMLHentKontaktinformasjonOgPreferanserResponse().withPerson(new XMLBruker()));
        assertThat(personService.hentEpostadresse(), is(""));
    }
}