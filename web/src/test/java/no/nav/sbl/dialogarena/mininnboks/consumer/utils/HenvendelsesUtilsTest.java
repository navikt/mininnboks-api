package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SVAR;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.TIL_HENVENDELSE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.tilXMLBehandlingsinformasjonV2;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class HenvendelsesUtilsTest {

    private static final String ID_1 = "1";
    private static final String ID_2 = "2";
    private static final String ID_3 = "3";
    private static final String ID_4 = "4";
    private static final String ID_5 = "5";
    private static final String ID_6 = "6";
    private static final String FODSELSNUMMER = "fodselsnummer-1234";
    private static final String FRITEKST = "fritekst";
    private static final DateTime OPPRETTET_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 1));
    private static final DateTime AVSLUTTET_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 2));
    private static final DateTime LEST_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 3));
    private static final Tema TEMA = Tema.FAMILIE_OG_BARN;
    private static final String KANAL = "kanal";

    @Test
    public void skalTransformereTilHenvendelseMedFelterForXMLSporsmal() {
        XMLBehandlingsinformasjonV2 info = mockXMLXMLBehandlingsinformasjonV2MedXMLSporsmal();
        List<XMLBehandlingsinformasjonV2> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe =  on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_1));
        assertThat(sporsmal.fodselsnummer, is(FODSELSNUMMER));
        assertThat(sporsmal.traadId, is(ID_1));
        assertThat(sporsmal.type, is(SPORSMAL));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.tema, is(TEMA));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.avsluttet, is(AVSLUTTET_DATO));
        assertTrue(sporsmal.erLest());
        assertNotNull(sporsmal.getLestDato());
        assertNull(sporsmal.kanal);
    }

    @Test
    public void skalTransformereTilHenvendelseMedFelterForXMLSvar() {
        XMLBehandlingsinformasjonV2 info = mockXMLXMLBehandlingsinformasjonV2MedXMLSvar();
        List<XMLBehandlingsinformasjonV2> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe =  on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_2));
        assertThat(sporsmal.fodselsnummer, is(FODSELSNUMMER));
        assertThat(sporsmal.traadId, is(ID_1));
        assertThat(sporsmal.type, is(SVAR));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.tema, is(TEMA));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.avsluttet, is(AVSLUTTET_DATO));
        assertThat(sporsmal.getLestDato(), is(LEST_DATO));
        assertTrue(sporsmal.erLest());
        assertNull(sporsmal.kanal);
    }

    @Test
    public void skalTransformereTilHenvendelseMedFelterForXMLReferat() {
        XMLBehandlingsinformasjonV2 info = mockXMLXMLBehandlingsinformasjonV2MedXMLReferat();
        List<XMLBehandlingsinformasjonV2> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe =  on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_3));
        assertThat(sporsmal.fodselsnummer, is(FODSELSNUMMER));
        assertThat(sporsmal.traadId, is(ID_3));
        assertThat(sporsmal.type, is(SAMTALEREFERAT));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.kanal, is(KANAL));
        assertThat(sporsmal.tema, is(TEMA));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.avsluttet, is(AVSLUTTET_DATO));
        assertThat(sporsmal.getLestDato(), is(LEST_DATO));
        assertTrue(sporsmal.erLest());
    }

    private XMLBehandlingsinformasjonV2 mockXMLXMLBehandlingsinformasjonV2MedXMLSporsmal() {
        return new XMLBehandlingsinformasjonV2()
                .withBehandlingsId(ID_1)
                .withAktor(new XMLAktor().withFodselsnummer(FODSELSNUMMER))
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLSporsmal()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMA.name())));
    }

    private XMLBehandlingsinformasjonV2 mockXMLXMLBehandlingsinformasjonV2MedXMLSvar() {
        return new XMLBehandlingsinformasjonV2()
                .withBehandlingsId(ID_2)
                .withAktor(new XMLAktor().withFodselsnummer(FODSELSNUMMER))
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLSvar()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMA.name())
                                .withSporsmalsId(ID_1)
                                .withLestDato(LEST_DATO)));
    }

    private XMLBehandlingsinformasjonV2 mockXMLXMLBehandlingsinformasjonV2MedXMLReferat() {
        return new XMLBehandlingsinformasjonV2()
                .withBehandlingsId(ID_3)
                .withAktor(new XMLAktor().withFodselsnummer(FODSELSNUMMER))
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLReferat()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMA.name())
                                .withLestDato(LEST_DATO)
                                .withKanal(KANAL)));
    }

    @Test
    public void skalOppretteXMLBehandlingsinformasjonV2MedRiktigeFelterForHenvendelseAvTypeSvar() {
        Henvendelse henvendelse = mockSvarHenvendelse();

        XMLBehandlingsinformasjonV2 info = tilXMLBehandlingsinformasjonV2(henvendelse);

        assertTrue(info.getMetadataListe().getMetadata().get(0) instanceof XMLSvar);
        XMLSvar svar = (XMLSvar) info.getMetadataListe().getMetadata().get(0);
        assertThat(info.getBehandlingsId(), is(ID_4));
        assertThat(info.getAktor().getFodselsnummer(), is(FODSELSNUMMER));
        assertThat(info.getHenvendelseType(), is(SVAR.name()));
        assertThat(info.getOpprettetDato(), is(OPPRETTET_DATO));
        assertThat(info.getAvsluttetDato(), is(AVSLUTTET_DATO));
        assertThat(svar.getTemagruppe(), is(TEMA.name()));
        assertThat(svar.getSporsmalsId(), is(ID_4));
        assertThat(svar.getFritekst(), is(FRITEKST));
        assertThat(svar.getLestDato(), is(LEST_DATO));
    }

    @Test
    public void skalOppretteXMLBehandlingsinformasjonV2MedRiktigeFelterForHenvendelseAvTypeReferat() {
        Henvendelse henvendelse = mockReferatHenvendelse();

        XMLBehandlingsinformasjonV2 info = tilXMLBehandlingsinformasjonV2(henvendelse);

        assertTrue(info.getMetadataListe().getMetadata().get(0) instanceof XMLReferat);
        XMLReferat referat = (XMLReferat) info.getMetadataListe().getMetadata().get(0);
        assertThat(info.getBehandlingsId(), is(ID_5));
        assertThat(info.getAktor().getFodselsnummer(), is(FODSELSNUMMER));
        assertThat(info.getHenvendelseType(), is(REFERAT.name()));
        assertThat(info.getOpprettetDato(), is(OPPRETTET_DATO));
        assertThat(info.getAvsluttetDato(), is(AVSLUTTET_DATO));
        assertThat(referat.getTemagruppe(), is(TEMA.name()));
        assertThat(referat.getKanal(), is(KANAL));
        assertThat(referat.getFritekst(), is(FRITEKST));
        assertThat(referat.getLestDato(), is(LEST_DATO));
    }

    @Test(expected = RuntimeException.class)
    public void skalKasteExceptionDersomHenvendelsestypenIkkeErSvarEllerReferat() {
        Henvendelse henvendelse = new Henvendelse(ID_6);
        henvendelse.type = Henvendelsetype.SPORSMAL;

        tilXMLBehandlingsinformasjonV2(henvendelse);
    }

    private Henvendelse mockSvarHenvendelse() {
        Henvendelse henvendelse = new Henvendelse(ID_4);
        henvendelse.traadId = ID_4;
        henvendelse.fodselsnummer = FODSELSNUMMER;
        henvendelse.fritekst = FRITEKST;
        henvendelse.tema = TEMA;
        henvendelse.opprettet = OPPRETTET_DATO;
        henvendelse.avsluttet = AVSLUTTET_DATO;
        henvendelse.markerSomLest(LEST_DATO);
        henvendelse.type = Henvendelsetype.SVAR;
        return henvendelse;
    }

    private Henvendelse mockReferatHenvendelse() {
        Henvendelse henvendelse = new Henvendelse(ID_5);
        henvendelse.traadId = ID_5;
        henvendelse.fodselsnummer = FODSELSNUMMER;
        henvendelse.fritekst = FRITEKST;
        henvendelse.kanal = KANAL;
        henvendelse.type = Henvendelsetype.REFERAT;
        henvendelse.tema = TEMA;
        henvendelse.opprettet = OPPRETTET_DATO;
        henvendelse.avsluttet = AVSLUTTET_DATO;
        henvendelse.markerSomLest(LEST_DATO);
        return henvendelse;
    }
}
