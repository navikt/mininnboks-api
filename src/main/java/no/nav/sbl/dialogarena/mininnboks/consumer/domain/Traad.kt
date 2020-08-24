package no.nav.sbl.dialogarena.mininnboks.consumer.domain;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.*;

public class Traad {
    private static final List<Henvendelsetype> FRA_NAV = asList(SPORSMAL_MODIA_UTGAAENDE, INFOMELDING_MODIA_UTGAAENDE, SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON, DOKUMENT_VARSEL);
    private static final List<Henvendelsetype> KAN_BESVARES = asList(SPORSMAL_MODIA_UTGAAENDE);

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
        this.kanBesvares = !nyeste.kassert && KAN_BESVARES.contains(nyeste.type);

        boolean avsluttet = FRA_NAV.contains(nyeste.type) && !kanBesvares;
        boolean ferdigstiltUtenSvar = Boolean.TRUE.equals(nyeste.ferdigstiltUtenSvar);
        this.avsluttet =  avsluttet || ferdigstiltUtenSvar;
        this.traadId = this.nyeste.traadId;
    }

    public static final Function<Traad, Date> NYESTE_OPPRETTET = traad -> traad.nyeste.opprettet;

    public static final Comparator<Traad> NYESTE_FORST = reverseOrder(comparing(Traad.NYESTE_OPPRETTET));
}
