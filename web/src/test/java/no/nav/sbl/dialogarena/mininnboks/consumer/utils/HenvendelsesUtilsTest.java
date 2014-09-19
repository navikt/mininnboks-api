package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.TIL_HENVENDELSE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class HenvendelsesUtilsTest {

    private static final String ID_1 = "1";
    private static final String ID_2 = "2";
    private static final String ID_3 = "3";
    private static final String FRITEKST = "fritekst";
    private static final DateTime OPPRETTET_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 1));
    private static final DateTime AVSLUTTET_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 2));
    private static final DateTime LEST_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 3));
    private static final Temagruppe TEMAGRUPPE = Temagruppe.FMLI;
    private static final String KANAL = "kanal";

    @Test
    public void skalTransformereTilHenvendelseMedFelterForXMLMeldingFraBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingFraBruker();
        List<XMLHenvendelse> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_1));
        assertThat(sporsmal.traadId, is(ID_1));
        assertThat(sporsmal.type, is(SPORSMAL_SKRIFTLIG));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.avsluttet, is(AVSLUTTET_DATO));
        assertTrue(sporsmal.erLest());
        assertNotNull(sporsmal.getLestDato());
        assertNull(sporsmal.kanal);
    }

    @Test
    public void skalTransformereTilHenvendelseMedFelterForXMLMeldingTilBrukerSomSvar() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBrukerSomSvar();
        List<XMLHenvendelse> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_2));
        assertThat(sporsmal.traadId, is(ID_1));
        assertThat(sporsmal.type, is(SVAR_SKRIFTLIG));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.avsluttet, is(AVSLUTTET_DATO));
        assertThat(sporsmal.getLestDato(), is(LEST_DATO));
        assertTrue(sporsmal.erLest());
        assertNull(sporsmal.kanal);
    }

    @Test
    public void skalTransformereTilHenvendelseMedFelterForXMLMeldingTilBrukerSomReferat() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBrukerSomReferat();
        List<XMLHenvendelse> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertThat(sporsmal.id, is(ID_3));
        assertThat(sporsmal.traadId, is(ID_3));
        assertThat(sporsmal.type, is(SAMTALEREFERAT_OPPMOTE));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.kanal, is(KANAL));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.avsluttet, is(AVSLUTTET_DATO));
        assertThat(sporsmal.getLestDato(), is(LEST_DATO));
        assertTrue(sporsmal.erLest());
    }

    private XMLHenvendelse mockXMLHenvendelseMedXMLMeldingFraBruker() {
        return new XMLHenvendelse()
                .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())
                .withBehandlingsId(ID_1)
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingFraBruker()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMAGRUPPE.name())));
    }

    private XMLHenvendelse mockXMLHenvendelseMedXMLMeldingTilBrukerSomSvar() {
        return new XMLHenvendelse()
                .withHenvendelseType(XMLHenvendelseType.SVAR_SKRIFTLIG.name())
                .withBehandlingsId(ID_2)
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withLestDato(LEST_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMAGRUPPE.name())
                                .withSporsmalsId(ID_1)));
    }

    private XMLHenvendelse mockXMLHenvendelseMedXMLMeldingTilBrukerSomReferat() {
        return new XMLHenvendelse()
                .withHenvendelseType(XMLHenvendelseType.REFERAT_OPPMOTE.name())
                .withBehandlingsId(ID_3)
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withLestDato(LEST_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMAGRUPPE.name())
                                .withKanal(KANAL)));
    }

}
