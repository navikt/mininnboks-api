package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.konto;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class KontonummerErMod11Test {

    private static final String GYLDIG_KONTONUMMER_REST_NULL        = "***REMOVED***";
    private static final String GYLDIG_KONTONUMMER_REST_ULIK_NULL   = "***REMOVED***";
    private static final String UGYLDIG_KONTONUMMER_1               = "***REMOVED***";


    @Test
    public void sjekkAvGyldigKontonummer() {
        KontonummerErMod11 erMod11 = new KontonummerErMod11();

        assertThat(erMod11.evaluate(GYLDIG_KONTONUMMER_REST_NULL), is(true));
        assertThat(erMod11.evaluate(GYLDIG_KONTONUMMER_REST_ULIK_NULL), is(true));
        assertThat(erMod11.evaluate(UGYLDIG_KONTONUMMER_1), is(false));
    }

}
