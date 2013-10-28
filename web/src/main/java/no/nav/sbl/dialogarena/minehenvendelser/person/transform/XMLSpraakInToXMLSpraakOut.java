package no.nav.sbl.dialogarena.minehenvendelser.person.transform;

import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLSpraak;
import org.apache.commons.collections15.Transformer;


public class XMLSpraakInToXMLSpraakOut implements Transformer<XMLSpraak, no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLSpraak> {
    @Override
    public no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLSpraak transform(XMLSpraak xmlSpraak) {
        return new no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLSpraak()
                .withKodeRef(xmlSpraak.getKodeRef())
                .withValue(xmlSpraak.getValue())
                .withKodeverksRef(xmlSpraak.getKodeverksRef());
    }
}