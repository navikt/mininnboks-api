package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Melding;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Meldingstype.SPORSMAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class InnboksVMTest {

    @Test
    public void nyesteHenvendelserITraadSkalReturnereRiktigAntallTraader() {
        List<Melding> meldinger = asList(createMeldingVM("1"), createMeldingVM("1"), createMeldingVM("1"), createMeldingVM("2"), createMeldingVM("4"));
        InnboksVM innboksVM = new InnboksVM(meldinger);
        assertThat(innboksVM.getNyesteHenvendelseITraad().size(), is(3));
    }

    @Test
    public void nyesteHenvendelserITraadSkalReturnereNyesteHenvendelseForst() {
        List<Melding> meldinger = asList(
                createMeldingVM("1", DateTime.now()),
                createMeldingVM("1", DateTime.now().minusDays(2)),
                createMeldingVM("1", DateTime.now().minusDays(5)),
                createMeldingVM("2", DateTime.now().minusDays(10)),
                createMeldingVM("4", DateTime.now().plusDays(2)));
        InnboksVM innboksVM = new InnboksVM(meldinger);
        assertThat(innboksVM.getNyesteHenvendelseITraad().get(0).melding.traadId, equalTo("4"));
        assertThat(innboksVM.getNyesteHenvendelseITraad().get(1).melding.traadId, equalTo("1"));
        assertThat(innboksVM.getNyesteHenvendelseITraad().get(2).melding.traadId, equalTo("2"));
    }

    @Test
    public void nyesteHenvendelserITraadHenterUtNyesteHenvendelseFraHverTraad() {
        Melding nyeste = createMeldingVM("1", DateTime.now());
        List<Melding> meldinger = asList(nyeste, createMeldingVM("1", DateTime.now().minusDays(1)), createMeldingVM("1", DateTime.now().minusDays(2)));
        InnboksVM innboksVM = new InnboksVM(meldinger);
        assertThat(innboksVM.getNyesteHenvendelseITraad().get(0).melding, equalTo(nyeste));
    }

    private Melding createMeldingVM(String traadid) {
        return createMeldingVM(traadid, DateTime.now());
    }

    private Melding createMeldingVM(String traaid, DateTime opprettet) {
        Random random = new Random();
        Melding melding = new Melding("" + random.nextInt(), SPORSMAL, traaid);
        melding.opprettet = opprettet;
        return melding;
    }
}
