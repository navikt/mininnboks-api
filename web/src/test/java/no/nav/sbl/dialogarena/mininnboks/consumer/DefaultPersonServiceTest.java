package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSFinnNAVKontorRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSFinnNAVKontorResponse;
import no.nav.tjeneste.virksomhet.person.v3.HentGeografiskTilknytningPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.HentGeografiskTilknytningResponse;
import no.nav.tjeneste.virksomhet.person.v3.HentGeografiskTilknytningSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentGeografiskTilknytningRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentGeografiskTilknytningResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPersonServiceTest {

    @Mock
    private PersonV3 personV3;
    @Mock
    private OrganisasjonEnhetV2 organisasjonEnhetV2;

    private PersonService.Default personService;

    @Before
    public void setUp() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());

        personService = new PersonService.Default(personV3, organisasjonEnhetV2);
    }

    @Test
    public void finnerNavKontor() throws FinnNAVKontorUgyldigInput, HentGeografiskTilknytningSikkerhetsbegrensing, HentGeografiskTilknytningPersonIkkeFunnet {
        String enhet = "1234";

        when(personV3.hentGeografiskTilknytning(any(WSHentGeografiskTilknytningRequest.class)))
                .thenReturn(new WSHentGeografiskTilknytningResponse().withGeografiskTilknytning(new WSGeografiskTilknytning() {
                    @Override
                    public String getGeografiskTilknytning() {
                        return super.getGeografiskTilknytning();
                    }
                }.withGeografiskTilknytning("")));

        when(organisasjonEnhetV2.finnNAVKontor(any(WSFinnNAVKontorRequest.class)))
                .thenReturn(new WSFinnNAVKontorResponse().withNAVKontor(new WSOrganisasjonsenhet().withEnhetId(enhet)));

        assertThat(personService.finnNavKontor().get(), is(enhet));
    }

    @Test(expected = RuntimeException.class)
    public void kasterRuntimeExceptionOmNAVKontorIkkeKanFinnes() throws FinnNAVKontorUgyldigInput {
        when(organisasjonEnhetV2.finnNAVKontor(any(WSFinnNAVKontorRequest.class)))
                .thenThrow(new FinnNAVKontorUgyldigInput());

        personService.finnNavKontor();
    }
}