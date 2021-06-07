package no.nav.sbl.dialogarena.mininnboks.consumer

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.spekframework.spek2.Spek

class TekstServiceImplTest : Spek({

    test("smoketest") {
        assertThat(TekstServiceImpl.hentTekster().size, Matchers.greaterThan(0) as Matcher<in Int?>?)
    }
})
