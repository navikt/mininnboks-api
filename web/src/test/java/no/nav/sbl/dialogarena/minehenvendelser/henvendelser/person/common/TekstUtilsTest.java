package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.common;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.konto.Formatering;
import org.junit.Test;

import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.common.TekstUtils.fjernSpesialtegn;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tester felles util-metoder
 */
public class TekstUtilsTest {

    @Test
    public void skalFjerneSpesialTegn() {
        assertThat(fjernSpesialtegn("1234 - 56 - ***REMOVED***."), is("123456***REMOVED***"));
    }

    @Test
    public void fjerneSpesialtegnFraNullErNull() {
        assertThat(fjernSpesialtegn(null), is(nullValue()));
    }

    @Test
    public void skalFormatereKontonummer() {
        assertThat(Formatering.formaterNorskKontonummer("123456***REMOVED***"), is("1234 56 ***REMOVED***"));
        assertThat(Formatering.formaterNorskKontonummer("  1234.56.***REMOVED***   "), is("1234 56 ***REMOVED***"));
    }

    @Test
    public void formateringAvKontonummerHarIngenValideringMenSetterKunInnSpacePaaRettSted() {
        assertThat(Formatering.formaterNorskKontonummer("1234. 56. ***REMOVED***123"), is("1234 56 ***REMOVED***123"));
    }

    @Test
    public void formaterNullReturnererNull() {
        assertThat(Formatering.formaterNorskKontonummer(null), is(nullValue()));
    }
}
