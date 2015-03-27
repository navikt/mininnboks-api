package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Svar;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController.NyHenvendelseResultat;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class HenvendelseControllerTest {
    @Mock
    HenvendelseService service;

    @InjectMocks
    HenvendelseController controller = new HenvendelseController();

    @Before
    public void setup() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        final List<Henvendelse> henvendelser = asList(
                new Henvendelse("1").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(now()),
                new Henvendelse("2").withTraadId("2").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(now()),
                new Henvendelse("3").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(now()),
                new Henvendelse("4").withTraadId("3").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(now()),
                new Henvendelse("5").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(now()),
                new Henvendelse("6").withTraadId("2").withType(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE).withOpprettetTid(now().plus(100)),
                new Henvendelse("7").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(now())
        );
        when(service.hentAlleHenvendelser(anyString())).thenReturn(henvendelser);
        when(service.hentTraad(anyString())).thenAnswer(new Answer<List<Henvendelse>>() {
            @Override
            public List<Henvendelse> answer(InvocationOnMock invocation) throws Throwable {
                String traadId = (String) invocation.getArguments()[0];

                return on(henvendelser)
                        .filter(where(Henvendelse.TRAAD_ID, equalTo(traadId)))
                        .collect();
            }
        });
        when(service.sendSvar(any(Henvendelse.class), anyString())).thenReturn(
                new WSSendInnHenvendelseResponse().withBehandlingsId(UUID.randomUUID().toString())
        );
    }

    @Test
    public void henterUtAlleHenvendelserOgGjorOmTilTraader() throws Exception {
        List<Traad> traader = controller.hentTraader();
        assertThat(traader.size(), is(3));
    }

    @Test
    public void serviceKanFeileUtenAtEndepunktFeiler() throws Exception {
        when(service.hentAlleHenvendelser(anyString())).thenReturn(null);
        List<Traad> traader = controller.hentTraader();
        assertThat(traader.size(), is(0));
    }

    @Test
    public void henterUtEnkeltTraadBasertPaId() throws Exception {
        Traad traad1 = (Traad) controller.hentEnkeltTraad("1").getEntity();
        Traad traad2 = (Traad) controller.hentEnkeltTraad("2").getEntity();
        Traad traad3 = (Traad) controller.hentEnkeltTraad("3").getEntity();

        assertThat(traad1.meldinger.size(), is(4));
        assertThat(traad2.meldinger.size(), is(2));
        assertThat(traad3.meldinger.size(), is(1));
    }

    @Test
    public void henterUtTraadSomIkkeFinnes() throws Exception {
        Response response = controller.hentEnkeltTraad("avabv");

        assertThat(response.getStatus(), is(404));
    }

    @Test
    public void markeringSomLest() throws Exception {
        controller.markerSomLest("1");

        verify(service, times(1)).merkSomLest("1");
    }

    @Test
    public void kanIkkeSendeSvarNarSisteHenvendelseIkkeErSporsmal() throws Exception {
        Svar svar = new Svar();
        svar.traadId = "1";
        svar.fritekst = "Tekst";
        HttpServletResponse resp = new MockHttpServletResponse(new MockHttpServletRequest(null, null, null));

        controller.sendSvar(svar, resp);

        assertThat(resp.getStatus(), is(Response.Status.NOT_ACCEPTABLE.getStatusCode()));
    }

    @Test
    public void kanSendeSvarNarSisteHenvendelseErSporsmal() throws Exception {
        Svar svar = new Svar();
        svar.traadId = "2";
        svar.fritekst = "Tekst";
        HttpServletResponse resp = new MockHttpServletResponse(new MockHttpServletRequest(null, null, null));

        NyHenvendelseResultat nyHenvendelseResultat = controller.sendSvar(svar, resp);

        assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(nyHenvendelseResultat.behandlingsId, is(not(nullValue())));
    }
}