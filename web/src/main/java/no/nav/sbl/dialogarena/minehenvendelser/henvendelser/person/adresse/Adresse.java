package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.adresse;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.common.LeggPaaTidspunkt;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public abstract class Adresse implements Serializable {

    protected final Adressetype type;

    private Optional<LocalDate> utlopsdato = none();

    protected Adresse(Adressetype type) {
        this.type = type;
    }

    public final Adressetype getType() {
        return type;
    }

    public final boolean is(Adressetype type) {
        return this.type == type;
    }

    public final boolean erUtgaatt() {
        LocalDate idag = LocalDate.now();
        return utlopsdato.isSome() && utlopsdato.get().isBefore(idag);
    }

    public final LocalDate getUtlopsdato() {
        return utlopsdato.getOrElse(null);
    }

    /**
     * Når verdier skal sendes til TPS må vi gå fra LocalDate til DateTime. Ettersom
     * utløpsdatoen er en til-og-med-dato, legger vi på tidspunktet helt på slutten av
     * dagen, 23:59:59, slik at semantikken i den opprinnelige datoen ivaretas
     * selv om det legges på tidspunkt.
     *
     * @return DateTime utlopstidspunkt med dato og klokkeslett.
     */
    public final DateTime getUtlopstidspunkt() {
        return utlopsdato.map(LeggPaaTidspunkt.SLUTTEN_AV_DAGEN).getOrElse(null);
    }

    public final void setUtlopsdato(LocalDate utlopsdato) {
        this.utlopsdato = optional(utlopsdato);
    }

    public abstract List<String> somAdresselinjer(Adressekodeverk kodeverk);

    public abstract String getLandkode();

    public static Transformer<Adresse, List<String>> toAdresselinjer(final Adressekodeverk kodeverk) {
        return new Transformer<Adresse, List<String>>() {
            @Override
            public List<String> transform(Adresse adresse) {
                return adresse.somAdresselinjer(kodeverk);
            }
        };
    }

    public static final Transformer<Adresse, Adressetype> GET_TYPE = new Transformer<Adresse, Adressetype>() {
        @Override
        public Adressetype transform(Adresse adresse) {
            return adresse.getType();
        }
    };

    public static final Transformer<Adresse, LocalDate> UTLOPSDATO = new Transformer<Adresse, LocalDate>() {
        @Override
        public LocalDate transform(Adresse adresse) {
            return adresse.getUtlopsdato();
        }
    };

    public static final Predicate<Adresse> UTGAATT = new Predicate<Adresse>() {
        @Override
        public boolean evaluate(Adresse adresse) {
            return adresse.erUtgaatt();
        }
    };

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }

}
