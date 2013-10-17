package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.adresse;

import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class GyldigUtlopsdatoTest {

    private final GyldigUtlopsdato gyldigUtlopsdato = new GyldigUtlopsdato();

    private static final LocalDate IDAG = new LocalDate(2013, 5, 25);

    @Test
    public void dagensDatoErIkkeGyldigUtlopsdato() {
        assertFalse(gyldigUtlopsdato.evaluate(IDAG));
    }

    @Test
    public void imorgenErGyldigUtlopsdato() {
        assertTrue(gyldigUtlopsdato.evaluate(IDAG.plusDays(1)));
    }

    @Test
    public void igaarErIkkeGyldigUtlopsdato() {
        assertFalse(gyldigUtlopsdato.evaluate(IDAG.minusDays(1)));
    }

    @Test
    public void ettAarFremITidFraOgMedDagensDatoErGyldigUtlopsdato() {
        assertTrue(gyldigUtlopsdato.evaluate(new LocalDate(IDAG.getYear() + 1, IDAG.getMonthOfYear(), IDAG.getDayOfMonth() - 1)));
    }

    @Test
    public void sammeDatoOmEtAarErIkkeGyldigUtlopsdato() {
        assertFalse(gyldigUtlopsdato.evaluate(new LocalDate(IDAG.getYear() + 1, IDAG.getMonthOfYear(), IDAG.getDayOfMonth())));
    }

    @Test
    public void ettAarOgEnDagFremITidErIkkeGyldigUtlopsdato() {
        assertFalse(gyldigUtlopsdato.evaluate(new LocalDate(IDAG.getYear() + 1, IDAG.getMonthOfYear(), IDAG.getDayOfMonth() + 1)));
    }


    @BeforeClass
    public static void freezeTime() {
        setCurrentMillisFixed(IDAG.toDate().getTime());
    }

    @AfterClass
    public static void resetToSystemClock() {
        setCurrentMillisSystem();
    }
}
