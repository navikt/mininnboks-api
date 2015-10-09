package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_SBL_INNGAAENDE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService.KONTAKT_NAV_SAKSTEMA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HenvendelseServiceTest {

    public static final String FNR = "fnr";
    public static final Temagruppe TEMAGRUPPE = Temagruppe.ARBD;
    public static final String FRITEKST = "fritekst";
    public static final String TRAAD_ID = "traadId";
    public static final String EKSTERN_AKTOR = "eksternAktor";
    public static final String TILKNYTTET_ENHET = "tilknyttetEnhet";
    public static final Boolean ER_TILKNYTTET_ANSATT = false;

    @Captor
    private ArgumentCaptor<WSSendInnHenvendelseRequest> sendInnHenvendelseRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<WSHentHenvendelseListeRequest> hentHenvendelseListeRequestArgumentCaptor;

    @Mock
    private HenvendelsePortType henvendelsePortType;
    @Mock
    private SendInnHenvendelsePortType sendInnHenvendelsePortType;
    @Mock
    private InnsynHenvendelsePortType innsynHenvendelsePortType;
    @Mock
    private PropertyResolver propertyResolver;

    private HenvendelseService.Default henvendelseService;

    @Before
    public void setUp() {
        henvendelseService = new HenvendelseService.Default(henvendelsePortType, sendInnHenvendelsePortType, innsynHenvendelsePortType, propertyResolver);

        List<Object> henvendelseListe = new ArrayList<>();
        henvendelseListe.add(new XMLHenvendelse().withHenvendelseType(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name()).withBehandlingsId("id"));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(
                new WSHentHenvendelseListeResponse().withAny(henvendelseListe));
        when(sendInnHenvendelsePortType.sendInnHenvendelse(any(WSSendInnHenvendelseRequest.class)))
                .thenReturn(new WSSendInnHenvendelseResponse().withBehandlingsId("id"));
    }

    @Test
    public void senderInnSporsmalMedRiktigeFelter() {
        Henvendelse henvendelse = new Henvendelse(FRITEKST, TEMAGRUPPE);

        henvendelseService.stillSporsmal(henvendelse, FNR);

        verify(sendInnHenvendelsePortType).sendInnHenvendelse(sendInnHenvendelseRequestArgumentCaptor.capture());
        WSSendInnHenvendelseRequest request = sendInnHenvendelseRequestArgumentCaptor.getValue();

        assertThat(request.getType(), is(SPORSMAL_SKRIFTLIG.name()));
        assertThat(request.getFodselsnummer(), is(FNR));
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) request.getAny();
        assertThat(xmlHenvendelse.getHenvendelseType(), is(SPORSMAL_SKRIFTLIG.name()));
        assertThat(xmlHenvendelse.getOpprettetDato(), is(notNullValue()));
        assertThat(xmlHenvendelse.getAvsluttetDato(), is(notNullValue()));
        assertThat(xmlHenvendelse.getTema(), is(KONTAKT_NAV_SAKSTEMA));
        assertThat(xmlHenvendelse.getBehandlingskjedeId(), is(nullValue()));
        XMLMeldingFraBruker meldingFraBruker = (XMLMeldingFraBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);
        assertThat(meldingFraBruker.getTemagruppe(), is(TEMAGRUPPE.name()));
        assertThat(meldingFraBruker.getFritekst(), is(FRITEKST));
    }

    @Test
    public void senderInnSvarMedRiktigeFelter() {
        Henvendelse henvendelse = new Henvendelse(FRITEKST, TEMAGRUPPE);
        henvendelse.traadId = TRAAD_ID;
        henvendelse.eksternAktor = EKSTERN_AKTOR;
        henvendelse.tilknyttetEnhet = TILKNYTTET_ENHET;
        henvendelse.erTilknyttetAnsatt = ER_TILKNYTTET_ANSATT;

        henvendelseService.sendSvar(henvendelse, FNR);

        verify(sendInnHenvendelsePortType).sendInnHenvendelse(sendInnHenvendelseRequestArgumentCaptor.capture());
        WSSendInnHenvendelseRequest request = sendInnHenvendelseRequestArgumentCaptor.getValue();

        assertThat(request.getType(), is(SVAR_SBL_INNGAAENDE.name()));
        assertThat(request.getFodselsnummer(), is(FNR));
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) request.getAny();
        assertThat(xmlHenvendelse.getHenvendelseType(), is(SVAR_SBL_INNGAAENDE.name()));
        assertThat(xmlHenvendelse.getOpprettetDato(), is(notNullValue()));
        assertThat(xmlHenvendelse.getAvsluttetDato(), is(notNullValue()));
        assertThat(xmlHenvendelse.getTema(), is(KONTAKT_NAV_SAKSTEMA));
        assertThat(xmlHenvendelse.getBehandlingskjedeId(), is(TRAAD_ID));
        assertThat(xmlHenvendelse.getEksternAktor(), is(EKSTERN_AKTOR));
        assertThat(xmlHenvendelse.getTilknyttetEnhet(), is(TILKNYTTET_ENHET));
        assertThat(xmlHenvendelse.isErTilknyttetAnsatt(), is(ER_TILKNYTTET_ANSATT));
        XMLMeldingFraBruker meldingFraBruker = (XMLMeldingFraBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);
        assertThat(meldingFraBruker.getTemagruppe(), is(TEMAGRUPPE.name()));
        assertThat(meldingFraBruker.getFritekst(), is(FRITEKST));
    }

    @Test
    public void sporOmRiktigFodselsnummerNaarDenHenterAlle() {
        henvendelseService.hentAlleHenvendelser(FNR);

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestArgumentCaptor.capture());
        WSHentHenvendelseListeRequest request = hentHenvendelseListeRequestArgumentCaptor.getValue();
        assertThat(request.getFodselsnummer(), is(FNR));
    }

    @Test
    public void sporOmAlleHenvendelsestyperNaarDenHenterAlle() {
        henvendelseService.hentAlleHenvendelser(FNR);

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestArgumentCaptor.capture());
        WSHentHenvendelseListeRequest request = hentHenvendelseListeRequestArgumentCaptor.getValue();
        List<XMLHenvendelseType> values = new ArrayList<>(asList(XMLHenvendelseType.values()));
        for (String type : request.getTyper()) {
            assertThat(values.contains(XMLHenvendelseType.fromValue(type)), is(true));
        }
    }
}
