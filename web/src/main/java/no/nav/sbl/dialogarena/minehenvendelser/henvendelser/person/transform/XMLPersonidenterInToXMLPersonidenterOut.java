package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;

import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPersonidenter;
import org.apache.commons.collections15.Transformer;

public final class XMLPersonidenterInToXMLPersonidenterOut implements Transformer<XMLPersonidenter, no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPersonidenter> {

    @Override
    public no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPersonidenter transform(XMLPersonidenter xmlPersonidenter) {
        return new no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPersonidenter().
                withKodeRef(xmlPersonidenter.getKodeRef()).
                withKodeverksRef(xmlPersonidenter.getKodeverksRef()).
                withValue(xmlPersonidenter.getValue());
    }
}