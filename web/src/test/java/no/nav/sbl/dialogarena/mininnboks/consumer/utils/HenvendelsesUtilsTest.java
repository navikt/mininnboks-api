package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.REFERAT;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SVAR;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.TIL_HENVENDELSE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

public class HenvendelsesUtilsTest {

    private static final String ID_1 = "1";
    private static final String ID_2 = "2";
    private static final String ID_3 = "3";
    private static final String FRITEKST = "fritekst";
    private static final DateTime OPPRETTET_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 1));
    private static final DateTime LEST_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 2));
    private static final Tema TEMA = Tema.FAMILIE_OG_BARN;
    private static final String KANAL = "kanal";

    @Test
    public void skalTransformereTilHendelseMedFelterForXMLSporsmal() {
        XMLBehandlingsinformasjonV2 info = mockXMLXMLBehandlingsinformasjonV2MedXMLSporsmal();
        List<XMLBehandlingsinformasjonV2> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe =  on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_1));
        assertThat(sporsmal.traadId, is(ID_1));
        assertThat(sporsmal.type, is(SPORSMAL));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.tema, is(TEMA));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertFalse(sporsmal.erLest());
        assertNull(sporsmal.kanal);
        assertNull(sporsmal.lestDato);
    }

    @Test
    public void skalTransformereTilHendelseMedFelterForXMLSvar() {
        XMLBehandlingsinformasjonV2 info = mockXMLXMLBehandlingsinformasjonV2MedXMLSvar();
        List<XMLBehandlingsinformasjonV2> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe =  on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_2));
        assertThat(sporsmal.traadId, is(ID_1));
        assertThat(sporsmal.type, is(SVAR));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.tema, is(TEMA));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.lestDato, is(LEST_DATO));
        assertTrue(sporsmal.erLest());
        assertNull(sporsmal.kanal);
    }

    @Test
    public void skalTransformereTilHendelseMedFelterForXMLReferat() {
        XMLBehandlingsinformasjonV2 info = mockXMLXMLBehandlingsinformasjonV2MedXMLReferat();
        List<XMLBehandlingsinformasjonV2> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe =  on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_3));
        assertThat(sporsmal.traadId, is(ID_3));
        assertThat(sporsmal.type, is(REFERAT));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.kanal, is(KANAL));
        assertThat(sporsmal.tema, is(TEMA));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.lestDato, is(LEST_DATO));
        assertTrue(sporsmal.erLest());
    }

    private XMLBehandlingsinformasjonV2 mockXMLXMLBehandlingsinformasjonV2MedXMLSporsmal() {
        return new XMLBehandlingsinformasjonV2()
                .withBehandlingsId(ID_1)
                .withOpprettetDato(OPPRETTET_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLSporsmal()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMA.name())));
    }

    private XMLBehandlingsinformasjonV2 mockXMLXMLBehandlingsinformasjonV2MedXMLSvar() {
        return new XMLBehandlingsinformasjonV2()
                .withBehandlingsId(ID_2)
                .withOpprettetDato(OPPRETTET_DATO)
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
                .withOpprettetDato(OPPRETTET_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLReferat()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMA.name())
                                .withLestDato(LEST_DATO)
                                .withKanal(KANAL)));
    }

}
