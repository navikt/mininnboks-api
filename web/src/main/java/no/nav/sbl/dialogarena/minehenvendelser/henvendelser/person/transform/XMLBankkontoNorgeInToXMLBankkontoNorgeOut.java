package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;


import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBankkontoNorge;
import org.apache.commons.collections15.Transformer;

public final class XMLBankkontoNorgeInToXMLBankkontoNorgeOut implements Transformer<XMLBankkontoNorge, no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoNorge> {
    @Override
    public no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoNorge transform(XMLBankkontoNorge xmlBankkontoNorge) {
        return new no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoNorge()
                .withBankkonto(new XMLBankkontonummerInToXMLBankkontonummerOut().transform(xmlBankkontoNorge.getBankkonto()));
    }
}