package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;


import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBankkontonummer;
import org.apache.commons.collections15.Transformer;

public final class XMLBankkontonummerInToXMLBankkontonummerOut implements Transformer<XMLBankkontonummer, no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontonummer> {
    @Override
    public no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontonummer transform(XMLBankkontonummer xmlBankkontonummer) {
        return new no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontonummer()
                .withBanknavn(xmlBankkontonummer.getBanknavn())
                .withBankkontonummer(xmlBankkontonummer.getBankkontonummer());
    }
}