package no.nav.sbl.dialogarena.minehenvendelser.person.consumer.transform;

import no.nav.sbl.dialogarena.minehenvendelser.person.adresse.StrukturertAdresse;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLMatrikkeladresse;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPostnummer;
import org.apache.commons.collections15.Transformer;

final class StrukturertAdresseToXMLMatrikkeladresse implements Transformer<StrukturertAdresse, XMLMatrikkeladresse> {

    @Override
    public XMLMatrikkeladresse transform(StrukturertAdresse adresse) {
        return new XMLMatrikkeladresse()
                .withPoststed(new XMLPostnummer().withValue(adresse.getPostnummer()))
                .withBolignummer(adresse.getBolignummer())
                .withEiendomsnavn(adresse.getOmraadeadresse());
    }
}
