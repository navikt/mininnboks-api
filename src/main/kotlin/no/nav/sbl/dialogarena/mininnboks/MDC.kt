package no.nav.sbl.dialogarena.mininnboks

import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import no.nav.common.log.LogFilter
import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import no.nav.common.utils.StringUtils

object MDC {
    // These are stolen from `no.nav.common.log.LogFilter`, but has private visibility in original file
    private const val RANDOM_USER_ID_COOKIE_NAME = "RUIDC";
    private const val ONE_MONTH_IN_SECONDS = 60 * 60 * 24 * 30;

    /**
     * Configures MDC with similar properties to `no.nav.common.log.LogFilter`,
     * which is needed in order to support MDC in other modules from common.
     */
    fun configure(config: CallLogging.Configuration) {
        with (config) {
            mdc(MDCConstants.MDC_CALL_ID) { call ->
                LogFilter.NAV_CALL_ID_HEADER_NAMES
                    .toList()
                    .map { call.request.header(it) }
                    .firstOrNull(StringUtils::notNullOrEmpty)
                    ?: IdUtils.generateId()
            }

            mdc(MDCConstants.MDC_USER_ID) { call ->
                val userId = call.request.cookies[RANDOM_USER_ID_COOKIE_NAME]
                if (userId == null && call.request.origin.scheme == "https") {
                    val generatedUserId = IdUtils.generateId()
                    call.response.cookies.append(
                        Cookie(
                            name = RANDOM_USER_ID_COOKIE_NAME,
                            path = "/",
                            maxAge = ONE_MONTH_IN_SECONDS,
                            httpOnly = true,
                            secure = true,
                            value = generatedUserId
                        )
                    )
                    generatedUserId
                } else {
                    userId
                }
            }

            mdc(MDCConstants.MDC_CONSUMER_ID) { call ->
                call.request.header(LogFilter.CONSUMER_ID_HEADER_NAME)
            }

            mdc(MDCConstants.MDC_REQUEST_ID) { call ->
                IdUtils.generateId()
            }
        }
    }
}
