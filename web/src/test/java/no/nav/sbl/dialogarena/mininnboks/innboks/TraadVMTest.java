package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM;
import org.apache.wicket.model.IModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.mininnboks.TestUtils.lagForsteHenvendelseITraad;
import static no.nav.sbl.dialogarena.mininnboks.TestUtils.lagHenvendelse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.joda.time.DateTime.now;
import static org.mockito.Mockito.verify;

public class TraadVMTest {

    private HenvendelseService service;

    @Before
    public void setUp() {
        service = Mockito.mock(HenvendelseService.class);
    }

    @Test
    public void skalSamleHenvendelserBasertPaaTraader() {
        Henvendelse henvendelse1 = lagForsteHenvendelseITraad();
        Henvendelse henvendelse2 = lagForsteHenvendelseITraad();

        String traadId1 = henvendelse1.traadId;
        String traadId2 = henvendelse2.traadId;

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
    public void filtrererUtTraaderSomIkkeHarEnRothenvendelse() {
        //Etter en viss periode (5 år i skrivende stund) skal henvendelser skjules helt. Derfor kan det hende at spørsmålet ikke kommer med når man spør Henvendelse.
        //Frittstående referater og spørsmål skal alltid ha behandlingskjedeId lik sin egen behandlingsId, så de skal ikke filtreres bort.

        Henvendelse henvendelse1 = lagForsteHenvendelseITraad();
        Henvendelse henvendelse2 = lagForsteHenvendelseITraad();

        String traadId1 = henvendelse1.traadId;
        String traadId2 = henvendelse2.traadId;

        Henvendelse henvendelse3 = lagHenvendelse(traadId1);
        Henvendelse henvendelse4 = lagHenvendelse(traadId2);
        Henvendelse henvendelse5 = lagHenvendelse(traadId1);

        List<TraadVM> traadListe = TraadVM.tilTraader(asList(henvendelse2, henvendelse3, henvendelse4, henvendelse5));

        assertThat(traadListe.size(), is(1));
    }

    @Test
    public void traaderErSortertPaaDato() {
        Henvendelse henvendelse1 = lagForsteHenvendelseITraad(now().minusDays(4));
        Henvendelse henvendelse2 = lagForsteHenvendelseITraad(now().minusDays(2));

        String traadId1 = henvendelse1.traadId;
        String traadId2 = henvendelse2.traadId;

        Henvendelse henvendelse3 = lagHenvendelse(traadId1, now().minusDays(1));
        Henvendelse henvendelse4 = lagHenvendelse(traadId2, now());

        List<TraadVM> traadListe = TraadVM.tilTraader(asList(henvendelse1, henvendelse2, henvendelse3, henvendelse4));

        assertThat(traadListe.get(0).id, is(traadId2));
        assertThat(traadListe.get(1).id, is(traadId1));
    }

    @Test
    public void henvendelserSortertIHverTraad() {
        Henvendelse henvendelse1 = lagForsteHenvendelseITraad(now().minusDays(1));

        String traadId = henvendelse1.traadId;

        Henvendelse henvendelse2 = lagHenvendelse(traadId, now().plusDays(1));
        Henvendelse henvendelse3 = lagHenvendelse(traadId, now());

        List<TraadVM> traadListe = TraadVM.tilTraader(asList(henvendelse1, henvendelse2, henvendelse3));

        assertThat(traadListe.get(0).henvendelser, is(asList(henvendelse2, henvendelse3, henvendelse1)));
    }

    @Test
    public void finnesDetUlesteHenvendelser() {
        List<Henvendelse> henvendelser = asList(lagHenvendelse(false), lagHenvendelse(true));
        IModel<Boolean> erLest = TraadVM.erLest(henvendelser);
        assertThat(erLest.getObject(), is(false));
    }

    @Test
    public void finnesDetKunLesteHenvendelser() {
        List<Henvendelse> henvendelser = asList(lagHenvendelse(true), lagHenvendelse(true));
        IModel<Boolean> erLest = TraadVM.erLest(henvendelser);
        assertThat(erLest.getObject(), is(true));
    }

    @Test
    public void setterAlleMeldingerSomLestDersomTraadMarkeresSomLest() {
        Henvendelse henvendelse1 = lagForsteHenvendelseITraad(now());
        Henvendelse henvendelse2 = lagHenvendelse(henvendelse1.id);
        List<TraadVM> traadVMListe = TraadVM.tilTraader(asList(henvendelse1, henvendelse2));

        assertThat(traadVMListe.size(), is(1));
        assertThat(henvendelse1.erLest(), is(false));
        assertThat(henvendelse2.erLest(), is(false));

        TraadVM traadVM = traadVMListe.get(0);
        traadVM.markerSomLest(service);

        verify(service).merkHenvendelseSomLest(henvendelse1);
        verify(service).merkHenvendelseSomLest(henvendelse2);
        assertThat(henvendelse1.erLest(), is(true));
        assertThat(henvendelse2.erLest(), is(true));
    }

    private static Optional<TraadVM> traadVMMedTraadId(String traadId, List<TraadVM> traadVMList) {
        for (TraadVM traadVM : traadVMList) {
            if (traadId.equals(traadVM.id)) {
                return optional(traadVM);
            }
        }
        return none();
    }

}
