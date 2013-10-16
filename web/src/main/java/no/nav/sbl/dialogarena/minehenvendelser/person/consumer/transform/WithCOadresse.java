package no.nav.sbl.dialogarena.minehenvendelser.person.consumer.transform;

import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLStrukturertAdresse;
import org.apache.commons.collections15.Transformer;

import static no.nav.sbl.dialogarena.minehenvendelser.person.adresse.StrukturertAdresse.ADRESSEEIERPREFIX;

class WithCOadresse implements Transformer<XMLStrukturertAdresse, XMLStrukturertAdresse> {

    private final Optional<String> adresseeier;

    WithCOadresse(Optional<String> adresseeier) {
        this.adresseeier = adresseeier;
    }

    @Override
    public XMLStrukturertAdresse transform(XMLStrukturertAdresse xmladresse) {
        for (String eier : adresseeier) {
            xmladresse.withTilleggsadresse(eier).withTilleggsadresseType(ADRESSEEIERPREFIX);
        }
        return xmladresse;
    }

}
