package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class TraadVMTest {


    @Test
    public void skalSamleHenvendelserBasertPaaTraader() {
        String traadId1 = "1";
        String traadId2 = "2";

        Henvendelse henvendelse1 = lagHenvendelse(traadId1);
        Henvendelse henvendelse2 = lagHenvendelse(traadId2);
        Henvendelse henvendelse3 = lagHenvendelse(traadId1);
        Henvendelse henvendelse4 = lagHenvendelse(traadId2);

        List<TraadVM> traadListe = TraadVM.tilTraader(asList(henvendelse1, henvendelse2, henvendelse3, henvendelse4));

        assertThat(traadListe.size(), is(2));

        TraadVM traad1 = traadVMMedTraadId(traadId1, traadListe).get();
        assertThat(traad1.henvendelser.size(), is(2));
        assertThat(traad1.henvendelser, hasItems(henvendelse1, henvendelse3));

        TraadVM traad2 = traadVMMedTraadId(traadId2, traadListe).get();
        assertThat(traad2.henvendelser.size(), is(2));
        assertThat(traad2.henvendelser, hasItems(henvendelse2, henvendelse4));
    }

    @Test
    public void traaderErSortertPaaDato() {
        String traadId1 = "1";
        String traadId2 = "2";

        Henvendelse henvendelse1 = lagHenvendelse(traadId1, DateTime.now().minusDays(1));
        Henvendelse henvendelse2 = lagHenvendelse(traadId2, DateTime.now());
        Henvendelse henvendelse3 = lagHenvendelse(traadId1, DateTime.now().minusDays(2));
        Henvendelse henvendelse4 = lagHenvendelse(traadId2, DateTime.now().minusDays(4));

        List<TraadVM> traadListe = TraadVM.tilTraader(asList(henvendelse1, henvendelse2, henvendelse3, henvendelse4));

        assertThat(traadListe.get(0).id, is(traadId2));
        assertThat(traadListe.get(1).id, is(traadId1));
    }

    @Test
    public void henvendelserSortertIHverTraad() {
        String traadId = "1";

        Henvendelse henvendelse1 = lagHenvendelse(traadId, DateTime.now());
        Henvendelse henvendelse2 = lagHenvendelse(traadId, DateTime.now().minusDays(1));
        Henvendelse henvendelse3 = lagHenvendelse(traadId, DateTime.now().plusDays(1));

        List<TraadVM> traadListe = TraadVM.tilTraader(asList(henvendelse1, henvendelse2, henvendelse3));

        assertThat(traadListe.get(0).henvendelser, is(asList(henvendelse3, henvendelse1, henvendelse2)));
    }


    private static Optional<TraadVM> traadVMMedTraadId(String traadId, List<TraadVM> traadVMList) {
        for (TraadVM traadVM : traadVMList) {
            if (traadId.equals(traadVM.id)) {
                return optional(traadVM);
            }
        }
        return none();
    }

    private static Henvendelse lagHenvendelse(String traadId) {
        return lagHenvendelse(traadId, DateTime.now());
    }

    private static Henvendelse lagHenvendelse(String traadId, DateTime opprettet) {
        Henvendelse henvendelse = new Henvendelse(UUID.randomUUID().toString());
        henvendelse.traadId = traadId;
        henvendelse.opprettet = opprettet;
        return henvendelse;
    }

}
