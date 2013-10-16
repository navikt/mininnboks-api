package no.nav.sbl.dialogarena.minehenvendelser.person.consumer.transform;

import no.nav.sbl.dialogarena.minehenvendelser.person.adresse.StrukturertAdresse;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPostnummer;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLStedsadresseNorge;
import org.apache.commons.collections15.Transformer;

final class StrukturertAdresseToXMLStedsadresseNorge implements Transformer<StrukturertAdresse, XMLStedsadresseNorge> {
    @Override
    public XMLStedsadresseNorge transform(StrukturertAdresse adresse) {
        return new XMLStedsadresseNorge()
                .withPoststed(new XMLPostnummer().withValue(adresse.getPostnummer()))
                .withBolignummer(adresse.getBolignummer());
    }
}
