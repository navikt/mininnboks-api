package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.kontaktdetaljer.Preferanser;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.BehandleBrukerprofilPortType;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkonto;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBruker;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.meldinger.XMLOppdaterKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBankkontoNorge;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBankkontoUtland;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBankkontonummer;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBankkontonummerUtland;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLNorskIdent;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPersonidenter;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPostadressetyper;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPreferanser;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLSpraak;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class OppdaterBrukerprofilConsumerTest {

    @Mock
    private BehandleBrukerprofilPortType webServiceMock;

    @Mock
    private BrukerprofilPortType brukerprofilServiceMock;

    private WebService webServiceStub = new WebService();
    private final OppdaterBrukerprofilConsumer service = new OppdaterBrukerprofilConsumer(webServiceStub);
    private static final LocalDate IDAG = new LocalDate(1981, 6, 24);

    private Person p;
    private no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker personFraTPS;

    @Before
    public void initIntegrationServiceWithMock() throws Exception {
        p = new Person();
        personFraTPS = (no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker) stubResponseFromService().getPerson();
        p.setPersonFraTPS(personFraTPS);
    }

    @Test
    public void senderIdentOgTypeSomErLikSomUthentetFraTPS() {
        service.oppdaterPerson(p);

        assertThat(webServiceStub.sistOppdatert.getIdent().getIdent(), is(personFraTPS.getIdent().getIdent()));
        assertThat(webServiceStub.sistOppdatert.getIdent().getType().getValue(), is(personFraTPS.getIdent().getType().getValue()));
    }

    @Test
    public void senderPostadresseTypeSomErLikSomPostadresseTypenUthentetFraTPS() {
        service.oppdaterPerson(p);

        assertThat(webServiceStub.sistOppdatert.getGjeldendePostadresseType().getValue(), is(personFraTPS.getGjeldendePostadresseType().getValue()));
    }

    @Test
    public void senderPreferansePaaPersonSomSkalOppdateres() {
        Preferanser preferanser = new Preferanser();
        preferanser.getMaalform().setValue("NO");
        preferanser.setElektroniskSamtykke(true);
        p.setPreferanser(preferanser);

        assertThat(p.getPersonFraTPS().getPreferanser().getMaalform().getValue(), is("SE"));
        assertThat(p.getPersonFraTPS().getPreferanser().isElektroniskKorrespondanse(), is(false));

        service.oppdaterPerson(p);

        assertThat(webServiceStub.sistOppdatert.getPreferanser().getMaalform().getValue(), is(p.getPreferanser().getMaalform().getValue()));
        assertThat(webServiceStub.sistOppdatert.getPreferanser().isElektroniskKorrespondanse(), is(p.getPreferanser().isElektroniskSamtykke()));
    }

    @Test
    public void senderBankkontoNorge() {

        service.oppdaterPerson(p);

        assertTrue(webServiceStub.sistOppdatert.getBankkonto() instanceof no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoNorge);
    }

    @Test
    public void senderBankkontoUtlandMedKontonummer() {
        String kontonummer = "123456";
        p.getPersonFraTPS().setBankkonto(new XMLBankkontoUtland().withBankkontoUtland(new XMLBankkontonummerUtland().withBankkontonummer(kontonummer)));

        service.oppdaterPerson(p);

        XMLBankkonto xmlbankkonto = webServiceStub.sistOppdatert.getBankkonto();

        assertTrue(xmlbankkonto instanceof no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoUtland);
        assertThat(((no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoUtland) xmlbankkonto).getBankkontoUtland().getBankkontonummer(), is(kontonummer));
    }

    @Test
    public void senderMedGjeldendePostadresse() {
        String adresse = "Thranes Gate 98";
        p.getPersonFraTPS().setGjeldendePostadresseType(new XMLPostadressetyper().withValue(adresse));

        service.oppdaterPerson(p);

        assertThat(webServiceStub.sistOppdatert.getGjeldendePostadresseType().getValue(), is(adresse));

    }

    @Test(expected = SystemException.class)
    public void sikkerhetsFeilWrappesISystemException() throws Exception {
        OppdaterBrukerprofilConsumer consumer = new OppdaterBrukerprofilConsumer(webServiceMock);

        doThrow(OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning.class)
                .when(webServiceMock).oppdaterKontaktinformasjonOgPreferanser(any(XMLOppdaterKontaktinformasjonOgPreferanserRequest.class));
        consumer.oppdaterPerson(p);
    }

    @Test(expected = ApplicationException.class)
    public void personFinnesIkkeWrappesIApplicationException() throws Exception {
        OppdaterBrukerprofilConsumer consumer = new OppdaterBrukerprofilConsumer(webServiceMock);

        doThrow(OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet.class)
                .when(webServiceMock).oppdaterKontaktinformasjonOgPreferanser(any(XMLOppdaterKontaktinformasjonOgPreferanserRequest.class));
        consumer.oppdaterPerson(p);
    }

    private XMLHentKontaktinformasjonOgPreferanserResponse stubResponseFromService() throws Exception {
        XMLHentKontaktinformasjonOgPreferanserResponse xmlResponse = new XMLHentKontaktinformasjonOgPreferanserResponse();
        xmlResponse.withPerson(new no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker()
                .withIdent(
                        new XMLNorskIdent().
                                withIdent("***REMOVED***").
                                withType(new XMLPersonidenter()
                                        .withValue("FOEDSELSNUMMER")))
                .withGjeldendePostadresseType(new XMLPostadressetyper().withValue("FOLKEREGISTRERT"))
                .withBankkonto(new XMLBankkontoNorge()
                        .withBankkonto(new XMLBankkontonummer()
                                .withBankkontonummer("123456789")))
                .withPreferanser(new XMLPreferanser()
                        .withMaalform(new XMLSpraak().withValue("SE"))
                        .withElektroniskKorrespondanse(false)
                ));
        return xmlResponse;
    }

    /*@Test
    public void senderIkkeEventuellMidlertidigAdresseNaarPersonHarValgtFolkeregistrertAdresse() {

        Person p = new Person("OLA NORMANN", "1234567809", optional(new StrukturertAdresse(BOSTEDSADRESSE)));
        p.setNorskMidlertidig(new StrukturertAdresse(GATEADRESSE, now().plusYears(1)));
        service.oppdaterPerson(p);
        assertThat(webServiceStub.sistOppdatert.getGjeldendePostadresseType().getValue(), is(GjeldendeAdresseKodeverk.BOSTEDSADRESSE.name()));
        assertThat(webServiceStub.sistOppdatert.getMidlertidigPostadresse(), nullValue());
    }

    @Test
    public void senderIkkeEventuellMidlertidigAdresseNaarPersonHarUkjentAdresse() {
        Person p = new Person("OLA NORMANN", "1234567809", ingenFolkeregistrertAdresse);
        service.oppdaterPerson(p);
        assertThat(webServiceStub.sistOppdatert.getGjeldendePostadresseType().getValue(), is(GjeldendeAdresseKodeverk.UKJENT_ADRESSE.name()));
        assertThat(webServiceStub.sistOppdatert.getMidlertidigPostadresse(), nullValue());
    }

    @Test
    public void senderMedMidlertidigNorskAdresseNaarBrukerHarValgtDet() {
        Person p = new Person("OLA NORMANN", "1234567809", ingenFolkeregistrertAdresse);
        p.setNorskMidlertidig(new StrukturertAdresse(Adressetype.OMRAADEADRESSE, OM_ET_AAR));
        p.velg(MIDLERTIDIG_NORGE);
        service.oppdaterPerson(p);
        assertThat(webServiceStub.sistOppdatert.getGjeldendePostadresseType().getValue(), is(GjeldendeAdresseKodeverk.MIDLERTIDIG_POSTADRESSE_NORGE.name()));
        XMLMidlertidigPostadresseNorge midlertidigPostadresse = (XMLMidlertidigPostadresseNorge) webServiceStub.sistOppdatert.getMidlertidigPostadresse();
        assertThat(midlertidigPostadresse.getPostleveringsPeriode().getFom(), is(IDAG.toDateTimeAtStartOfDay()));
        assertThat(midlertidigPostadresse.getPostleveringsPeriode().getTom(), is(OM_ET_AAR.toDateTime(new LocalTime(23, 59, 59))));
        assertThat(midlertidigPostadresse.getStrukturertAdresse().getTilleggsadresseType(), nullValue());
        assertThat(midlertidigPostadresse.getStrukturertAdresse().getTilleggsadresse(), nullValue());

        p.setNorskMidlertidig(new StrukturertAdresse((Adressetype.POSTBOKSADRESSE)));
        p.velg(MIDLERTIDIG_NORGE);
        service.oppdaterPerson(p);
        assertThat(webServiceStub.sistOppdatert.getGjeldendePostadresseType().getValue(), is(GjeldendeAdresseKodeverk.MIDLERTIDIG_POSTADRESSE_NORGE.name()));

        p.setNorskMidlertidig(new StrukturertAdresse(Adressetype.GATEADRESSE));
        p.velg(MIDLERTIDIG_NORGE);
        service.oppdaterPerson(p);
        assertThat(webServiceStub.sistOppdatert.getGjeldendePostadresseType().getValue(), is(GjeldendeAdresseKodeverk.MIDLERTIDIG_POSTADRESSE_NORGE.name()));
    }

    @Test
    public void senderMedUtenlandskMidlertidigAdresseNaarBrukerHarValgtDet() {
        Person p = new Person("OLA NORMANN", "1234567809", ingenFolkeregistrertAdresse);
        p.setUtenlandskMidlertidig(new UstrukturertAdresse(Adressetype.UTENLANDSK_ADRESSE, OM_ET_AAR, "UK", "Oxford Street"));
        p.velg(MIDLERTIDIG_UTLAND);
        service.oppdaterPerson(p);
        assertThat(webServiceStub.sistOppdatert.getGjeldendePostadresseType().getValue(), is(GjeldendeAdresseKodeverk.MIDLERTIDIG_POSTADRESSE_UTLAND.name()));
        assertThat(webServiceStub.sistOppdatert.getMidlertidigPostadresse().getPostleveringsPeriode().getFom(), is(IDAG.toDateTimeAtStartOfDay()));
        assertThat(webServiceStub.sistOppdatert.getMidlertidigPostadresse().getPostleveringsPeriode().getTom(), is(OM_ET_AAR.toDateTime(new LocalTime(23, 59, 59))));
    }

    @Test
    public void personForsokerAaRegistrereMidlertidigAdresseLikDenFolkeregistrerte() throws Exception {
        Person person = new Person("Bønna", "***REMOVED***", ingenFolkeregistrertAdresse);

        int thrownExceptions = 0;
        List<String> feilkoder = MIDLERTIDIG_ADRESSE_LIK_FOLKEREGISTRERT.feilkoder;
        assertThat("Ikke interessant å teste dersom feilkoder er tom", feilkoder, not(empty()));
        for (String feilkode : feilkoder) {
            try {
                OppdaterKontaktinformasjonOgPreferanserUgyldigInput ugyldigInput = new OppdaterKontaktinformasjonOgPreferanserUgyldigInput(null,
                        new XMLUgyldigInput().withFeilaarsak(feilkode));
                doThrow(ugyldigInput).when(webServiceMock).oppdaterKontaktinformasjonOgPreferanser(any(XMLOppdaterKontaktinformasjonOgPreferanserRequest.class));
                new OppdaterBrukerprofilConsumer(webServiceMock).oppdaterPerson(person);
            } catch (TpsValideringException e) {
                assertThat(e.messagekey, is(MIDLERTIDIG_ADRESSE_LIK_FOLKEREGISTRERT.feilmeldingMsgKey));
                thrownExceptions++;
            }
        }
        assertThat("har kastet exception for hver feilkode", feilkoder, hasSize(thrownExceptions));
    }

    @Test
    public void senderXMLStedsadresseNaarOmraadeadresseErTom() {
        Person per = new Person("Per Hansen", "123456***REMOVED***", ingenFolkeregistrertAdresse);
        StrukturertAdresse adresse = new StrukturertAdresse(OMRAADEADRESSE);
        adresse.setPostnummer("7000");
        adresse.setUtlopsdato(now().plusMonths(1));
        per.setNorskMidlertidig(adresse);
        per.velg(MIDLERTIDIG_NORGE);
        service.oppdaterPerson(per);
        XMLMidlertidigPostadresseNorge midlertidigPostadresse = (XMLMidlertidigPostadresseNorge) webServiceStub.sistOppdatert.getMidlertidigPostadresse();
        assertTrue(midlertidigPostadresse.getStrukturertAdresse() instanceof XMLStedsadresseNorge);
    }

    @Test
    public void senderXMLMatrikkeladresseNaarPersonHarOmraadeadresse() {
        Person paal = new Person("Paal Hansen", "123456***REMOVED***", ingenFolkeregistrertAdresse);
        StrukturertAdresse adresse = new StrukturertAdresse(OMRAADEADRESSE);
        adresse.setOmraadeadresse("Gården");
        adresse.setPostnummer("7000");
        adresse.setUtlopsdato(now().plusMonths(1));
        paal.setNorskMidlertidig(adresse);
        paal.velg(MIDLERTIDIG_NORGE);
        service.oppdaterPerson(paal);
        XMLMidlertidigPostadresseNorge midlertidigPostadresse = (XMLMidlertidigPostadresseNorge) webServiceStub.sistOppdatert.getMidlertidigPostadresse();
        assertTrue(midlertidigPostadresse.getStrukturertAdresse() instanceof XMLMatrikkeladresse);
    }

    @Test
    public void setterTilleggsadresseNaarAdresseeierErSatt() {
        Person p = new Person("OLA NORMANN", "1234567809", ingenFolkeregistrertAdresse);
        StrukturertAdresse adresse = new StrukturertAdresse(Adressetype.OMRAADEADRESSE, OM_ET_AAR);
        adresse.setAdresseeier("Bergljot Gudvangsplass");
        p.setNorskMidlertidig(adresse);
        p.velg(MIDLERTIDIG_NORGE);
        service.oppdaterPerson(p);
        XMLMidlertidigPostadresseNorge midlertidigPostadresse = (XMLMidlertidigPostadresseNorge) webServiceStub.sistOppdatert.getMidlertidigPostadresse();
        assertThat(midlertidigPostadresse.getStrukturertAdresse().getTilleggsadresseType(), is(StrukturertAdresse.ADRESSEEIERPREFIX));
        assertThat(midlertidigPostadresse.getStrukturertAdresse().getTilleggsadresse(), is("Bergljot Gudvangsplass"));
    }*/


    @BeforeClass
    public static void setupDates() {
        DateTimeUtils.setCurrentMillisFixed(IDAG.toDate().getTime());
    }

    @AfterClass
    public static void resetToSystemClock() {
        DateTimeUtils.setCurrentMillisSystem();
    }
}

class WebService implements BehandleBrukerprofilPortType {

    XMLBruker sistOppdatert;

    @Override
    public void ping() {
    }


    @Override
    public void oppdaterKontaktinformasjonOgPreferanser(XMLOppdaterKontaktinformasjonOgPreferanserRequest request) {
        sistOppdatert = (XMLBruker) request.getPerson();
    }
}