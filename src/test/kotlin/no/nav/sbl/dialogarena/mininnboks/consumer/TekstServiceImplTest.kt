package no.nav.sbl.dialogarena.mininnboks.consumer

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

class TekstServiceImplTest {
    @Test
    fun smoketest() {
        MatcherAssert.assertThat(TekstServiceImpl.hentTekster()?.size, Matchers.greaterThan(0) as Matcher<in Int?>?)
    }


    @Test
    fun basicTest() {
        "tekster/mininnboks".asResource {
            // test on `it` here...
            println(it)
        }

    }

    fun String.asResource(work: (String) -> Unit) {
        val path = "tekster/mininnboks"
        val content = object {}.javaClass.classLoader.getResources(path)
        work(content.toList().size.toString())
    }
}
