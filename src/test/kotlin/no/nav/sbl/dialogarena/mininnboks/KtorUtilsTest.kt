package no.nav.sbl.dialogarena.mininnboks

import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.SubjectHandler
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.spekframework.spek2.Spek

object KtorUtilsTest : Spek({
    test("skal ha tilgang på subject i async-blokken") {
        runBlocking {
            externalCall(TestUtils.MOCK_SUBJECT) {
                val subject = SubjectHandler.getSubject()
                assertThat(subject.isPresent, `is`(true))
                assertThat(subject.get().uid, `is`("12345678901"))
            }
        }
    }
})
