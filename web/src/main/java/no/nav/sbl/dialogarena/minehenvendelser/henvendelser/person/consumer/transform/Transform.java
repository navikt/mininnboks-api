package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform;

import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.feil.XMLForretningsmessigUnntak;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBankkontoNorge;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLElektroniskAdresse;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLElektroniskKommunikasjonskanal;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLKodeverdi;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLPeriode;
import org.apache.commons.collections15.Transformer;
import org.joda.time.LocalDate;

import static no.nav.modig.lang.collections.TransformerUtils.first;

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

    public static Transformer<XMLPeriode, LocalDate> sluttdato() {
        return new XMLPeriodeTransform.Sluttdato();
    }

    public static Transformer<XMLKodeverdi, String> kodeverdi() {
        return new XMLKodeverdiTransform.Verdi();
    }

    public static Transformer<String, XMLBankkontoNorge> toXMLBankkontoNorge() {
        return new StringToXMLBankkontoNorge();
    }

    public static Transformer<XMLElektroniskAdresse, XMLElektroniskKommunikasjonskanal> toXMLElektroniskKommunkasjonskanal() {
        return new XMLElektroniskAdresseToXMLElektroniskKommunikasjonskanal();
    }

    public static Transformer<String, XMLElektroniskKommunikasjonskanal> toEpostKommunikasjonskanal() {
        return first(new StringToXMLEpost()).then(toXMLElektroniskKommunkasjonskanal());
    }

    private Transform() { }
}
