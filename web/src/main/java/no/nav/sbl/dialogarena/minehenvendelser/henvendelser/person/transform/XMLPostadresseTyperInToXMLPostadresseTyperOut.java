package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;

import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPostadressetyper;
import org.apache.commons.collections15.Transformer;

public final class XMLPostadresseTyperInToXMLPostadresseTyperOut implements Transformer<XMLPostadressetyper, no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPostadressetyper> {
    @Override
    public no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPostadressetyper transform(XMLPostadressetyper xmlPostadressetyper) {
        return new no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPostadressetyper()
                .withValue(xmlPostadressetyper.getValue());
    }
}