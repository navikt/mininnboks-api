package no.nav.sbl.dialogarena.mininnboks.consumer

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

class TekstServiceImplTest {
    @Test
    fun smoketest() {
        MatcherAssert.assertThat(TekstServiceImpl.hentTekster().size, Matchers.greaterThan(0) as Matcher<in Int?>?)
    }
}
