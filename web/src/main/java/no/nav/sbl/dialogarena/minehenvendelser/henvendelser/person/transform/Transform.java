package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;

import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.feil.XMLForretningsmessigUnntak;
import org.apache.commons.collections15.Transformer;

public final class Transform {

    public static Transformer<XMLForretningsmessigUnntak, String> feilaarsakkode() {
        return new XMLUnntakTransform.Aarsakkode();
    }

    private Transform() { }
}