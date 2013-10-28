package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelse;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelsetype.SPORSMAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class InnboksVMTest {

    @Test
    public void nyesteHenvendelserITraadSkalReturnereRiktigAntallTraader() {
        List<Henvendelse> meldinger = asList(createMeldingVM("1"), createMeldingVM("1"), createMeldingVM("1"), createMeldingVM("2"), createMeldingVM("4"));
        InnboksVM innboksVM = new InnboksVM(meldinger);
        assertThat(innboksVM.getNyesteHenvendelseITraad().size(), is(3));
    }

    @Test
    public void nyesteHenvendelserITraadSkalReturnereNyesteHenvendelseForst() {
        List<Henvendelse> meldinger = asList(
                createMeldingVM("1", DateTime.now()),
                createMeldingVM("1", DateTime.now().minusDays(2)),
                createMeldingVM("1", DateTime.now().minusDays(5)),
                createMeldingVM("2", DateTime.now().minusDays(10)),
                createMeldingVM("4", DateTime.now().plusDays(2)));
        InnboksVM innboksVM = new InnboksVM(meldinger);
        assertThat(innboksVM.getNyesteHenvendelseITraad().get(0).henvendelse.traadId, equalTo("4"));
        assertThat(innboksVM.getNyesteHenvendelseITraad().get(1).henvendelse.traadId, equalTo("1"));
        assertThat(innboksVM.getNyesteHenvendelseITraad().get(2).henvendelse.traadId, equalTo("2"));
    }

    @Test
    public void nyesteHenvendelserITraadHenterUtNyesteHenvendelseFraHverTraad() {
        Henvendelse nyeste = createMeldingVM("1", DateTime.now());
        List<Henvendelse> meldinger = asList(nyeste, createMeldingVM("1", DateTime.now().minusDays(1)), createMeldingVM("1", DateTime.now().minusDays(2)));
        InnboksVM innboksVM = new InnboksVM(meldinger);
        assertThat(innboksVM.getNyesteHenvendelseITraad().get(0).henvendelse, equalTo(nyeste));
    }

    private Henvendelse createMeldingVM(String traadid) {
        return createMeldingVM(traadid, DateTime.now());
    }

    private Henvendelse createMeldingVM(String traaid, DateTime opprettet) {
        Random random = new Random();
        Henvendelse henvendelse = new Henvendelse("" + random.nextInt(), SPORSMAL, traaid);
        henvendelse.opprettet = opprettet;
        return henvendelse;
    }
}
