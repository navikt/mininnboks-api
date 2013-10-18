package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;


import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPreferanser;
import org.apache.commons.collections15.Transformer;

public class XMLPreferanserInToXMLPreferanserOut implements Transformer<XMLPreferanser, no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPreferanser> {
    @Override
    public no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPreferanser transform(XMLPreferanser xmlPreferanser) {
        return new no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPreferanser()
                .withElektroniskKorrespondanse(xmlPreferanser.isElektroniskKorrespondanse())
                .withMaalform(new XMLSpraakInToXMLSpraakOut().transform(xmlPreferanser.getMaalform()));
    }
}
