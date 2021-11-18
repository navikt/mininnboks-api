package no.nav.sbl.dialogarena.mininnboks.common.audit

import io.ktor.application.*
import io.ktor.util.pipeline.*
import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditIdentifier.*
import org.slf4j.LoggerFactory
import java.util.*

private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
class Audit {
    open class AuditResource(val resource: String)
    enum class Action {
        CREATE, READ, UPDATE, DELETE
    }

    interface AuditDescriptor<T> {
        fun log()
        fun denied(reason: String)
        fun failed(exception: Throwable)

        fun Throwable.getFailureReason(): String = this.message ?: this.toString()
    }

    internal class Descriptor(
        private val subject: Subject?,
        private val action: Action,
        private val resourceType: AuditResource,
        private val identifiers: Array<out Pair<AuditIdentifier, String?>>
    ) : AuditDescriptor<Any> {
        override fun log() {
            logInternal(subject, action, resourceType, identifiers)
        }

        override fun denied(reason: String) {
            logInternal(subject, action, resourceType, arrayOf(DENY_REASON to reason))
        }

        override fun failed(exception: Throwable) {
            logInternal(subject, action, resourceType, arrayOf(FAIL_REASON to exception.getFailureReason()))
        }
    }

    companion object {
        fun describe(subject: Subject?, action: Action, resourceType: AuditResource, vararg identifiers: Pair<AuditIdentifier, String?>): AuditDescriptor<Any> {
            return Descriptor(subject, action, resourceType, identifiers)
        }

        suspend fun PipelineContext<Unit, ApplicationCall>.withAudit(descriptor: AuditDescriptor<Any>, block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit) {
            val scope = this
            try {
                block(scope)
                descriptor.log()
            } catch (e: Exception) {
                descriptor.failed(e)
                throw e
            }
        }

        private fun logInternal(subject: Subject?, action: Action, resourceType: AuditResource, identifiers: Array<out Pair<AuditIdentifier, String?>>) {
            val logline = listOfNotNull(
                "action='$action'",
                subject?.let { "subject='${it.uid}'" },
                "resource='${resourceType.resource}'",
                *identifiers
                    .map { "${it.first}='${it.second ?: "-"}'" }
                    .toTypedArray()
            )
                .joinToString(" ")

            tjenestekallLogg.info(logline)
        }
    }
}
