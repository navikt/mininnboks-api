package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.*;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.*;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.TIL_HENVENDELSE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class HenvendelsesUtilsTest {

    private static final String ID_1 = "1";
    private static final String ID_2 = "2";
    private static final String ID_3 = "3";
    private static final String ID_4 = "4";
    private static final String ID_5 = "5";
    private static final String FRITEKST = "fritekst";
    private static final DateTime OPPRETTET_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 1));
    private static final DateTime AVSLUTTET_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 2));
    private static final DateTime LEST_DATO = new DateTime(new GregorianCalendar(Calendar.YEAR, 1, 3));
    private static final Temagruppe TEMAGRUPPE = Temagruppe.FMLI;
    private static final String KANAL = "kanal";
    private static final String NAVIDENT = "navident";

    @Test
    public void transformererXMLHenvendelseSomSporsmalFraBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, ID_1, ID_1);
        List<XMLHenvendelse> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertStandardFelter(sporsmal);
        assertThat(sporsmal.id, is(ID_1));
        assertThat(sporsmal.traadId, is(ID_1));
        assertThat(sporsmal.type, is(SPORSMAL_SKRIFTLIG));
        assertThat(sporsmal.getLestDato(), is(notNullValue()));
        assertThat(sporsmal.erLest(), is(true));
        assertThat(sporsmal.kanal, is(nullValue()));
    }

    @Test
    public void transformererXMLHenvendelseSomSvarFraBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, ID_2, ID_2);
        List<XMLHenvendelse> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse svar = henvendelserListe.get(0);

        assertStandardFelter(svar);
        assertThat(svar.id, is(ID_2));
        assertThat(svar.traadId, is(ID_2));
        assertThat(svar.type, is(SVAR_SBL_INNGAAENDE));
        assertThat(svar.getLestDato(), is(notNullValue()));
        assertThat(svar.erLest(), is(true));
        assertThat(svar.kanal, is(nullValue()));
    }

    @Test
    public void transformererXMLHenvendelseSomSporsmalTilBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, ID_3, ID_3);
        List<XMLHenvendelse> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse svar = henvendelserListe.get(0);

        assertStandardFelter(svar);
        assertThat(svar.id, is(ID_3));
        assertThat(svar.traadId, is(ID_3));
        assertThat(svar.type, is(SPORSMAL_MODIA_UTGAAENDE));
        assertThat(svar.getLestDato(), is(LEST_DATO));
        assertThat(svar.erLest(), is(true));
        assertThat(svar.kanal, is(KANAL));
    }

    @Test
    public void transformererXMLHenvendelseSomSvarTilBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.SVAR_SKRIFTLIG, ID_4, ID_1);
        List<XMLHenvendelse> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse svar = henvendelserListe.get(0);

        assertStandardFelter(svar);
        assertThat(svar.id, is(ID_4));
        assertThat(svar.traadId, is(ID_1));
        assertThat(svar.type, is(SVAR_SKRIFTLIG));
        assertThat(svar.getLestDato(), is(LEST_DATO));
        assertThat(svar.erLest(), is(true));
        assertThat(svar.kanal, is(KANAL));
    }

    @Test
    public void transformererXMLHenvendelseSomReferatTilBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE, ID_5, ID_5);
        List<XMLHenvendelse> infoList = Arrays.asList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(TIL_HENVENDELSE).collect();
        Henvendelse referat = henvendelserListe.get(0);

        assertStandardFelter(referat);
        assertThat(referat.id, is(ID_5));
        assertThat(referat.traadId, is(ID_5));
        assertThat(referat.type, is(SAMTALEREFERAT_OPPMOTE));
        assertThat(referat.getLestDato(), is(LEST_DATO));
        assertThat(referat.erLest(), is(true));
        assertThat(referat.kanal, is(KANAL));
    }

    @Test
    public void taklerAtInnholdetErBlittSlettet() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE, ID_5, ID_5);
        info.setMetadataListe(null);

        Henvendelse referat = TIL_HENVENDELSE.transform(info);

        assertThat(referat.fritekst, nullValue());
        assertThat(referat.temagruppe, nullValue());
    }

    private void assertStandardFelter(Henvendelse sporsmal) {
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.avsluttet, is(AVSLUTTET_DATO));
    }

    private XMLHenvendelse mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType type, String id, String kjedeId) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withBehandlingsId(id)
                .withBehandlingskjedeId(kjedeId)
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingFraBruker()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMAGRUPPE.name())
                ));
    }

    private XMLHenvendelse mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType type, String id, String kjedeId) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withBehandlingsId(id)
                .withBehandlingskjedeId(kjedeId)
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withLestDato(LEST_DATO)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMAGRUPPE.name())
                                .withKanal(KANAL)
                                .withNavident(NAVIDENT)
                ));
    }

}
