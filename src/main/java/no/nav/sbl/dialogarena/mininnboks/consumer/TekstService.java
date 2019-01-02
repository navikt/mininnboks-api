package no.nav.sbl.dialogarena.mininnboks.consumer;

import java.util.Map;

public interface TekstService {

    String hentTekst(String key);

    Map<String, String> hentTekster();

}
