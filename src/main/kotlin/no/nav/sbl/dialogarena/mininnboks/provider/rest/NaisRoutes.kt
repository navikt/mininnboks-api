package no.nav.sbl.dialogarena.mininnboks.provider.rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.health.selftest.SelfTestUtils
import no.nav.common.health.selftest.SelftestHtmlGenerator

fun Route.naisRoutes(readinessCheck: () -> Boolean,
                     livenessCheck: () -> Boolean = { true },
                     selftestChecks: List<SelfTestCheck> = emptyList(),
                     collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry
) {

    get("/isAlive") { checkRespond({ livenessCheck() }, "Alive", "Not alive") }
    get("/isReady") { checkRespond({ readinessCheck() }, "Ready", "Not ready") }

    metrics(collectorRegistry)

    selfTest(selftestChecks)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.checkRespond(condition: () -> Boolean, successMessage: String, failureMessage: String) {
    if (condition()) {
        call.respondText(successMessage)
    } else {
        call.respondText(failureMessage, status = HttpStatusCode.InternalServerError)
    }
}

private fun Route.metrics(collectorRegistry: CollectorRegistry) {
    get("/metrics") {
        val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: setOf()
        call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
            TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
        }
    }
}

private fun Route.selfTest(selftestChecks: List<SelfTestCheck>) {
    get("/selftest") {
        withContext(Dispatchers.IO) {
            val selftest = SelfTestUtils.checkAllParallel(selftestChecks)
            val selftestMarkup = SelftestHtmlGenerator.generate(selftest)

            call.respondText(selftestMarkup, ContentType.Text.Html)
        }
    }
}
