package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.konto;

import org.apache.commons.collections15.Predicate;
import org.junit.Test;

import static no.nav.modig.lang.collections.PredicateUtils.both;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.konto.GyldigNorskKontonummer.ELLEVE_SIFFER;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.konto.GyldigNorskKontonummer.OPPFYLLER_MOD11;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GyldigNorskKontonummerTest {

    private final Predicate<String> gyldigKontonummer = both(ELLEVE_SIFFER).and(OPPFYLLER_MOD11);

    @Test
    public void kontonummerMaaVaere11Tall() {
        assertThat(gyldigKontonummer.evaluate("***REMOVED***"), is(true));
        assertThat(gyldigKontonummer.evaluate("***REMOVED***"), is(true));

        assertThat(gyldigKontonummer.evaluate("53681042"), is(false));
        assertThat(gyldigKontonummer.evaluate("***REMOVED***"), is(false));
        assertThat(gyldigKontonummer.evaluate("ab681042373"), is(false));
        assertThat(gyldigKontonummer.evaluate("ab681042373"), is(false));
        assertThat(gyldigKontonummer.evaluate("ab***REMOVED***"), is(false));
    }

    @Test
    public void kontonummerKanHaSeparatorTegn() {
        assertThat(gyldigKontonummer.evaluate("5368 10 42373"), is(true));
        assertThat(gyldigKontonummer.evaluate("5368.10.42373"), is(true));
        assertThat(gyldigKontonummer.evaluate("5368,10,42373"), is(true));
        assertThat(gyldigKontonummer.evaluate("5368-10-42373"), is(true));

        assertThat(gyldigKontonummer.evaluate("5368a10a42373"), is(false));
    }

    @Test
    public void kontonummerMaaVaereEtGyldigKontonummer() {
        assertThat(gyldigKontonummer.evaluate("***REMOVED***"), is(false));
        assertThat(gyldigKontonummer.evaluate("***REMOVED***"), is(false));
    }
}