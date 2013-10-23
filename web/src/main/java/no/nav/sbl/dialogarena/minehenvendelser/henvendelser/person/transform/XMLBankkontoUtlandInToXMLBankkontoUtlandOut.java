package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;


import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBankkontoUtland;
import org.apache.commons.collections15.Transformer;

public class XMLBankkontoUtlandInToXMLBankkontoUtlandOut implements Transformer<XMLBankkontoUtland, no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoUtland> {
    @Override
    public no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoUtland transform(XMLBankkontoUtland xmlBankkontoUtland) {
        return new no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoUtland()
                .withBankkontoUtland(new XMLBankkontonummerUtlandInToXMLBankkontonummerUtlandOut().transform(xmlBankkontoUtland.getBankkontoUtland()));
    }
}