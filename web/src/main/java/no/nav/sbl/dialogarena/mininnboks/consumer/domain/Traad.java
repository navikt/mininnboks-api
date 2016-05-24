package no.nav.sbl.dialogarena.mininnboks.consumer.domain;

import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.FRA_NAV;

public class Traad {
    public final String traadId;
    public final List<Henvendelse> meldinger;
    public final Henvendelse nyeste, eldste;
    public final Boolean kanBesvares, avsluttet;

    public Traad(List<Henvendelse> meldinger) {
        this.meldinger = meldinger.stream()
                .sorted(NYESTE_OVERST)
                .collect(toList());
        this.nyeste = this.meldinger.get(0);
        this.eldste = this.meldinger.get(this.meldinger.size() - 1);
        this.kanBesvares = SPORSMAL_MODIA_UTGAAENDE.equals(nyeste.type);
        this.avsluttet = FRA_NAV.contains(nyeste.type) && !kanBesvares;
        this.traadId = this.nyeste.traadId;
    }

    public static final Function<Traad, DateTime> NYESTE_HENVENDELSE = traad -> Henvendelse.OPPRETTET.apply(traad.nyeste);

    public static final Comparator<Traad> NYESTE_FORST = reverseOrder(comparing(Traad.NYESTE_HENVENDELSE));
}
