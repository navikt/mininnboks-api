package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform;

import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.feil.XMLForretningsmessigUnntak;
import org.apache.commons.collections15.Transformer;

public final class Transform {

    /**
     * Transformer som caster til en angitt type dersom dette er mulig. Hvis ikke
     * så returneres null. Dette gjør det mulig å "short-circuit'e" en map-operasjon
     * med {@link Optional}.
     *
     * @param <T> en type man vil caste til
     * @param type typen man vil caste til.
     * @return Samme instans castet til subtype, eller <code>null</code> dersom casting ikke er mulig.
     *
     */
    public static <T> Transformer<Object, T> castIfPossibleTo(final Class<T> type) {
        return new Transformer<Object, T>() {
            @Override
            public T transform(Object value) {
                return type.isInstance(value) ? type.cast(value) : null;
            }
        };
    }

    public static Transformer<XMLForretningsmessigUnntak, String> feilaarsakkode() {
        return new XMLUnntakTransform.Aarsakkode();
    }

    private Transform() { }
}
