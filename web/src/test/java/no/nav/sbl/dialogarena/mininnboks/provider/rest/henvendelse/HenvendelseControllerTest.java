package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController.NyHenvendelseResultat;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.join;
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

    @Mock
    CmsContentRetriever cmsContentRetriever = mock(CmsContentRetriever.class);

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

        HenvendelsesUtils.setCmsContentRetriever(cmsContentRetriever);

        when(service.hentAlleHenvendelser(anyString())).thenReturn(henvendelser);

        when(service.hentTraad(anyString())).thenAnswer((Answer<List<Henvendelse>>) invocation -> {
            String traadId = (String) invocation.getArguments()[0];

            return henvendelser.stream()
                    .filter(henvendelse -> traadId.equals(henvendelse.traadId))
                    .collect(toList());
        });

        when(service.sendSvar(any(Henvendelse.class), anyString())).thenReturn(
                new WSSendInnHenvendelseResponse().withBehandlingsId(UUID.randomUUID().toString())
        );
    }

    @After
    public void after() {
        HenvendelsesUtils.setCmsContentRetriever(null);
    }

    @Test
    public void henterUtAlleHenvendelserOgGjorOmTilTraader() throws Exception {
        List<Traad> traader = controller.hentTraader();
        assertThat(traader.size(), is(3));
    }

    @Test
    public void serviceKanFeileUtenAtEndepunktFeiler() throws Exception {
        when(service.hentAlleHenvendelser(anyString())).thenReturn(emptyList());
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
        controller.markerAlleSomLest("1");

        verify(service, times(1)).merkAlleSomLest("1");
    }

    @Test
    public void kanIkkeSendeSvarNarSisteHenvendelseIkkeErSporsmal() throws Exception {
        Svar svar = new Svar();
        svar.traadId = "1";
        svar.fritekst = "Tekst";

        Response response = controller.sendSvar(svar);

        assertThat(response.getStatus(), is(Response.Status.NOT_ACCEPTABLE.getStatusCode()));
    }

    @Test
    public void kanSendeSvarNarSisteHenvendelseErSporsmal() throws Exception {
        Svar svar = new Svar();
        svar.traadId = "2";
        svar.fritekst = "Tekst";


        NyHenvendelseResultat nyHenvendelseResultat = ((NyHenvendelseResultat) controller.sendSvar(svar).getEntity());

        assertThat(nyHenvendelseResultat.behandlingsId, is(not(nullValue())));
    }

    @Test
    public void kopiererNyesteErTilknyttetAnsattFlaggTilSvaret() {
        Henvendelse henvendelse1 = new Henvendelse("1");
        henvendelse1.erTilknyttetAnsatt = true;
        henvendelse1.opprettet = now();
        henvendelse1.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;
        Henvendelse henvendelse2 = new Henvendelse("2");
        henvendelse2.erTilknyttetAnsatt = false;
        henvendelse2.opprettet = now();
        List<Henvendelse> henvendelser = asList(henvendelse1, henvendelse2);
        when(service.hentTraad(anyString())).thenReturn(henvendelser);

        Svar svar = new Svar();
        svar.fritekst = "fritekst";
        svar.traadId = "0";
        controller.sendSvar(svar);

        ArgumentCaptor<Henvendelse> henvendelseArgumentCaptor = ArgumentCaptor.forClass(Henvendelse.class);
        verify(service).sendSvar(henvendelseArgumentCaptor.capture(), anyString());

        assertThat(henvendelseArgumentCaptor.getValue().erTilknyttetAnsatt, is(true));
    }

    @Test
    public void kopiererBrukersEnhetTilSvaret() {
        String brukersEnhet = "1234";

        Henvendelse henvendelse1 = new Henvendelse("1");
        henvendelse1.opprettet = now().minusDays(1);
        henvendelse1.type = Henvendelsetype.SPORSMAL_SKRIFTLIG;
        henvendelse1.brukersEnhet = brukersEnhet;
        Henvendelse henvendelse2 = new Henvendelse("2");
        henvendelse2.opprettet = now();
        henvendelse2.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;
        when(service.hentTraad(anyString())).thenReturn(asList(henvendelse1, henvendelse2));

        Svar svar = new Svar();
        svar.fritekst = "fritekst";
        svar.traadId = "0";
        controller.sendSvar(svar);

        ArgumentCaptor<Henvendelse> henvendelseArgumentCaptor = ArgumentCaptor.forClass(Henvendelse.class);
        verify(service).sendSvar(henvendelseArgumentCaptor.capture(), anyString());

        assertThat(henvendelseArgumentCaptor.getValue().brukersEnhet, is(brukersEnhet));
    }

    @Test(expected = AssertionError.class)
    public void smellerHvisTomFritekstISporsmal() {
        Sporsmal sporsmal = new Sporsmal();
        sporsmal.fritekst = "";
        sporsmal.temagruppe = Temagruppe.ARBD.name();
        controller.sendSporsmal(sporsmal, new MockHttpServletResponse());
    }

    @Test(expected = AssertionError.class)
    public void smellerHvisForLangFritekstISporsmal() {
        Sporsmal sporsmal = new Sporsmal();
        sporsmal.fritekst = join(nCopies(1001, 'a'), "");
        sporsmal.temagruppe = Temagruppe.ARBD.name();
        controller.sendSporsmal(sporsmal, new MockHttpServletResponse());
    }

    @Test(expected = AssertionError.class)
    public void smellerHvisAndreSosialtjenesterTemagruppeISporsmal() {
        Sporsmal sporsmal = new Sporsmal();
        sporsmal.fritekst = "DUMMY";
        sporsmal.temagruppe = Temagruppe.ANSOS.name();
        controller.sendSporsmal(sporsmal, new MockHttpServletResponse());
    }
}