package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.adresse.StrukturertAdresse;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLGateadresse;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPostnummer;
import org.apache.commons.collections15.Transformer;

import java.math.BigInteger;

import static no.nav.modig.lang.option.Optional.optional;

final class StrukturertAdresseToXMLGateadresse implements Transformer<StrukturertAdresse, XMLGateadresse> {

    @Override
    public XMLGateadresse transform(StrukturertAdresse adresse) {
        return new XMLGateadresse()
            .withGatenavn(adresse.getGatenavn())
            .withHusnummer(optional(adresse.getGatenummer()).map(TO_BIGINTEGER).getOrElse(null))
            .withBolignummer(adresse.getBolignummer())
            .withHusbokstav(adresse.getHusbokstav())
            .withPoststed(new XMLPostnummer().withValue(adresse.getPostnummer()));
    }


    private static final Transformer<String, BigInteger> TO_BIGINTEGER = new Transformer<String, BigInteger>() {
        @Override
        public BigInteger transform(String s) {
            return new BigInteger(s);
        }
    };

}
