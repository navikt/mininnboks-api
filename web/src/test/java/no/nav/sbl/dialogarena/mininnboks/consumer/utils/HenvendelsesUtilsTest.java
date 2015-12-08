package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.*;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.cleanOutHtml;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.tilHenvendelse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private static final String TILKNYTTET_ENHET = "tilknyttetEnhet";
    private static final Boolean ER_TILKNYTTET_ANSATT = false;
    private static final String BRUKERS_ENHET = "1234";
    private static final String KONTORSPERRE_ENHET = "kontorsperreEnhet";

    private PropertyResolver propertyResolver = mock(PropertyResolver.class);

    @Before
    public void setup() {
        when(propertyResolver.getProperty(anyString())).thenReturn("value");
    }

    @Test
    public void transformererXMLHenvendelseSomSporsmalFraBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, ID_1, ID_1);
        List<XMLHenvendelse> infoList = Collections.singletonList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(tilHenvendelse(propertyResolver)).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertStandardFelter(sporsmal);
        assertThat(sporsmal.id, is(ID_1));
        assertThat(sporsmal.traadId, is(ID_1));
        assertThat(sporsmal.type, is(SPORSMAL_SKRIFTLIG));
        assertThat(sporsmal.getLestDato(), is(notNullValue()));
        assertThat(sporsmal.isLest(), is(true));
        assertThat(sporsmal.kanal, is(nullValue()));
        assertThat(sporsmal.brukersEnhet, is(BRUKERS_ENHET));
    }

    @Test
    public void transformererXMLHenvendelseSomSvarFraBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, ID_2, ID_2);
        List<XMLHenvendelse> infoList = Collections.singletonList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(tilHenvendelse(propertyResolver)).collect();
        Henvendelse svar = henvendelserListe.get(0);

        assertStandardFelter(svar);
        assertThat(svar.id, is(ID_2));
        assertThat(svar.traadId, is(ID_2));
        assertThat(svar.type, is(SVAR_SBL_INNGAAENDE));
        assertThat(svar.getLestDato(), is(notNullValue()));
        assertThat(svar.isLest(), is(true));
        assertThat(svar.kanal, is(nullValue()));
        assertThat(svar.brukersEnhet, is(BRUKERS_ENHET));
        assertThat(svar.kontorsperreEnhet, is(KONTORSPERRE_ENHET));
    }

    @Test
    public void transformererXMLHenvendelseSomSporsmalTilBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, ID_3, ID_3);
        List<XMLHenvendelse> infoList = Collections.singletonList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(tilHenvendelse(propertyResolver)).collect();
        Henvendelse sporsmal = henvendelserListe.get(0);

        assertStandardFelter(sporsmal);
        assertThat(sporsmal.id, is(ID_3));
        assertThat(sporsmal.traadId, is(ID_3));
        assertThat(sporsmal.type, is(SPORSMAL_MODIA_UTGAAENDE));
        assertThat(sporsmal.getLestDato(), is(LEST_DATO));
        assertThat(sporsmal.isLest(), is(true));
        assertThat(sporsmal.kanal, is(KANAL));
        assertThat(sporsmal.eksternAktor, is(NAVIDENT));
        assertThat(sporsmal.tilknyttetEnhet, is(TILKNYTTET_ENHET));
        assertThat(sporsmal.erTilknyttetAnsatt, is(ER_TILKNYTTET_ANSATT));
        assertThat(sporsmal.brukersEnhet, is(BRUKERS_ENHET));

    }

    @Test
    public void transformererXMLHenvendelseSomSvarTilBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.SVAR_SKRIFTLIG, ID_4, ID_1);
        List<XMLHenvendelse> infoList = Collections.singletonList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(tilHenvendelse(propertyResolver)).collect();
        Henvendelse svar = henvendelserListe.get(0);

        assertStandardFelter(svar);
        assertThat(svar.id, is(ID_4));
        assertThat(svar.traadId, is(ID_1));
        assertThat(svar.type, is(SVAR_SKRIFTLIG));
        assertThat(svar.getLestDato(), is(LEST_DATO));
        assertThat(svar.isLest(), is(true));
        assertThat(svar.kanal, is(KANAL));
        assertThat(svar.brukersEnhet, is(BRUKERS_ENHET));
    }

    @Test
    public void transformererXMLHenvendelseSomReferatTilBruker() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE, ID_5, ID_5);
        List<XMLHenvendelse> infoList = Collections.singletonList(info);

        List<Henvendelse> henvendelserListe = on(infoList).map(tilHenvendelse(propertyResolver)).collect();
        Henvendelse referat = henvendelserListe.get(0);

        assertStandardFelter(referat);
        assertThat(referat.id, is(ID_5));
        assertThat(referat.traadId, is(ID_5));
        assertThat(referat.type, is(SAMTALEREFERAT_OPPMOTE));
        assertThat(referat.getLestDato(), is(LEST_DATO));
        assertThat(referat.isLest(), is(true));
        assertThat(referat.kanal, is(KANAL));
        assertThat(referat.brukersEnhet, is(BRUKERS_ENHET));
    }

    @Test
    public void hvisInnholdetErBorteBlirHenvendelsenMerketSomKassert() {
        XMLHenvendelse info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE, ID_5, ID_5);
        info.setMetadataListe(null);

        when(propertyResolver.getProperty("innhold.kassert")).thenReturn("Innholdet er kassert");
        when(propertyResolver.getProperty("temagruppe.kassert")).thenReturn("Kassert");
        Henvendelse referat = tilHenvendelse(propertyResolver).transform(info);

        assertThat(referat.fritekst, is("Innholdet er kassert"));
        assertThat(referat.statusTekst, is("Kassert"));
        assertThat(referat.temagruppe, nullValue());
    }

    private void assertStandardFelter(Henvendelse sporsmal) {
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
        assertThat(sporsmal.opprettet, is(OPPRETTET_DATO));
        assertThat(sporsmal.avsluttet, is(AVSLUTTET_DATO));
    }

    @Test
    public void vaskerHtmlIFritekstMenBevarerLineEndings() {
        String tekst = "<h1>Hei</h1> \n Hallo";
        String cleanTekst = cleanOutHtml(tekst);
        assertThat(cleanTekst, is("Hei \n Hallo"));
    }

    private XMLHenvendelse mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType type, String id, String kjedeId) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withBehandlingsId(id)
                .withBehandlingskjedeId(kjedeId)
                .withOpprettetDato(OPPRETTET_DATO)
                .withAvsluttetDato(AVSLUTTET_DATO)
                .withBrukersEnhet(BRUKERS_ENHET)
                .withKontorsperreEnhet(KONTORSPERRE_ENHET)
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
                .withEksternAktor(NAVIDENT)
                .withBrukersEnhet(BRUKERS_ENHET)
                .withTilknyttetEnhet(TILKNYTTET_ENHET)
                .withErTilknyttetAnsatt(ER_TILKNYTTET_ANSATT)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMAGRUPPE.name())
                                .withKanal(KANAL)
                                .withNavident(NAVIDENT)
                ));
    }

}
