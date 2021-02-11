package no.nav.sbl.dialogarena.mininnboks.consumer

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

class TekstServiceImplTest {
    @Test
    fun smoketest() {
        assertThat(TekstServiceImpl.hentTekster().size, Matchers.greaterThan(0) as Matcher<in Int?>?)
    }
}
