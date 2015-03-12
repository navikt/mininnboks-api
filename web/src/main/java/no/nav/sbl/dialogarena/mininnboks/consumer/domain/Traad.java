package no.nav.sbl.dialogarena.mininnboks.consumer.domain;

import org.apache.commons.collections15.Transformer;

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.TransformerUtils.first;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.FRA_NAV;

public class Traad {
    public final List<Henvendelse> meldinger;
    public final Henvendelse nyeste;
    public final Boolean kanBesvares, avsluttet;

    public Traad(List<Henvendelse> meldinger) {
        this.meldinger = on(meldinger).collect(NYESTE_OVERST);
        this.nyeste = this.meldinger.get(0);
        this.kanBesvares = SPORSMAL_MODIA_UTGAAENDE.equals(nyeste.type);
        this.avsluttet = FRA_NAV.contains(nyeste.type) && !kanBesvares;
    }

    public static final Transformer<Traad, Henvendelse> NYESTE = new Transformer<Traad, Henvendelse>() {
        @Override
        public Henvendelse transform(Traad traad) {
            return traad.nyeste;
        }
    };

    public static final Comparator<Traad> NYESTE_FORST = reverseOrder(compareWith(first(Traad.NYESTE).then(Henvendelse.OPPRETTET)));

}
