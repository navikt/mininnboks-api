package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.common.auth.SubjectHandler;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.feil.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningRequest;
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
    private PersonV3 personV3;
    private PersonService.Default personService;

    @Before
    public void setUp() {
        personService = new PersonService.Default(personV3);
    }

    @Test
    public void henterEnhet() throws Exception {
        String enhet = "1234";

        SubjectHandler.withSubject(MOCK_SUBJECT, () -> {
            assertThat(personService.hentGeografiskTilknytning().get(), is(enhet));
        });
    }

    @Test(expected = RuntimeException.class)
    public void kasterRuntimeExceptionOmEnhetIkkeKanhentes() throws Exception {

        when(personV3.hentGeografiskTilknytning(any(HentGeografiskTilknytningRequest.class))).thenThrow(new HentGeografiskTilknytningPersonIkkeFunnet("En feil", new PersonIkkeFunnet()));

        personService.hentGeografiskTilknytning();
    }

}
