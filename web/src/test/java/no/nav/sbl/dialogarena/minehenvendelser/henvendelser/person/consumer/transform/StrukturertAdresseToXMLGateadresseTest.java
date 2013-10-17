package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.adresse.Adressetype;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.adresse.StrukturertAdresse;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLGateadresse;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLMidlertidigPostadresseNorge;
import org.apache.commons.collections15.Transformer;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform.Transform.toXMLGateadresse;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform.Transform.toXMLMatrikkeladresse;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform.Transform.toXMLMidlertidigGateadresse;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform.Transform.toXMLStedsadresseNorge;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.joda.time.DateTime.now;


public class StrukturertAdresseToXMLGateadresseTest {

    private static final String GATENAVN = "GATENAVN";
    private static final String BOLIGNUMMER = "1";
    private static final String GATENUMMER = "2";
    private static final String POSTNUMMER = "1234";
    private static final String HUSBOKSTAV = "F";

    @Test
    public void testTransform() throws Exception {

        StrukturertAdresse adresse = new StrukturertAdresse(Adressetype.GATEADRESSE);
        adresse.setGatenavn(GATENAVN);
        adresse.setBolignummer(BOLIGNUMMER);
        adresse.setGatenummer(GATENUMMER);
        adresse.setPostnummer(POSTNUMMER);
        adresse.setHusbokstav(HUSBOKSTAV);

        XMLGateadresse xmlGateAdresse = toXMLGateadresse().transform(adresse);
        assertThat(xmlGateAdresse.getBolignummer(), is(equalTo(BOLIGNUMMER)));
        assertThat(xmlGateAdresse.getGatenavn(), is(equalTo(GATENAVN)));
        assertThat(xmlGateAdresse.getHusnummer(), is(equalTo(new BigInteger(GATENUMMER))));
        assertThat(xmlGateAdresse.getHusbokstav(), is(equalTo(HUSBOKSTAV)));

    }

    @Test
    public void setterIkkeTilleggsadressetypeForAdresserUtenAdresseeier() {
        Optional<String> ingenEier = none();
        List<Transformer<StrukturertAdresse, XMLMidlertidigPostadresseNorge>> adresseTransformers = asList(
                toXMLMidlertidigGateadresse(now(), ingenEier), toXMLStedsadresseNorge(now(), ingenEier), toXMLMatrikkeladresse(now(), ingenEier));
        int tests = 0;
        for (Transformer<StrukturertAdresse, XMLMidlertidigPostadresseNorge> transformer : adresseTransformers) {
            XMLMidlertidigPostadresseNorge xmlGateAdresse = transformer.transform(new StrukturertAdresse(Adressetype.UKJENT_ADRESSE));
            assertThat(xmlGateAdresse.getStrukturertAdresse().getTilleggsadresseType(), nullValue());
            assertThat(xmlGateAdresse.getStrukturertAdresse().getTilleggsadresse(), nullValue());
            tests++;
        }
        assertThat(tests, equalTo(adresseTransformers.size()));
    }

    @Test
    public void setterTilleggsadressetypeForAdresserMedAdresseeier() {
        List<Transformer<StrukturertAdresse, XMLMidlertidigPostadresseNorge>> adresseTransformers = asList(
                toXMLMidlertidigGateadresse(now(), optional("Per")), toXMLStedsadresseNorge(now(), optional("Per")), toXMLMatrikkeladresse(now(), optional("Per")));
        int tests = 0;
        for (Transformer<StrukturertAdresse, XMLMidlertidigPostadresseNorge> transformer : adresseTransformers) {
            XMLMidlertidigPostadresseNorge xmlGateAdresse = transformer.transform(new StrukturertAdresse(Adressetype.UKJENT_ADRESSE));
            assertThat(xmlGateAdresse.getStrukturertAdresse().getTilleggsadresseType(), is(StrukturertAdresse.ADRESSEEIERPREFIX));
            assertThat(xmlGateAdresse.getStrukturertAdresse().getTilleggsadresse(), is("Per"));
            tests++;
        }
        assertThat(tests, equalTo(adresseTransformers.size()));
    }
}
