package no.nav.sbl.dialogarena.minehenvendelser.person.transform;

import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBankkontonummerUtland;
import org.apache.commons.collections15.Transformer;

public final class XMLBankkontonummerUtlandInToXMLBankkontonummerUtlandOut implements Transformer<XMLBankkontonummerUtland, no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontonummerUtland> {
    @Override
    public no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontonummerUtland transform(XMLBankkontonummerUtland xmlBankkontonummerUtland) {
        return new no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontonummerUtland()
                .withBankkontonummer(xmlBankkontonummerUtland.getBankkontonummer()).withBanknavn(xmlBankkontonummerUtland.getBanknavn());
    }
}