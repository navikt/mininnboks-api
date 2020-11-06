package no.nav.sbl.dialogarena.mininnboks

import com.auth0.jwk.JwkProvider
import com.natpryce.konfig.*

private val defaultProperties = ConfigurationMap(
        mapOf(
                "JWKS_URL" to "JWKS_URL",
                "ISSUER" to "ISSUER",
                "SERVICEGATEWAY_URL" to "SERVICEGATEWAY_URL",
                "INNSYN_HENVENDELSE_WS_URL" to "innsyn.henvendelse.ws.url",
                "HENVENDELSE_WS_URL" to "henvendelse.ws.url",
                "SEND_INN_HENVENDELSE_WS_URL" to "send.inn.henvendelse.ws.url",
                "PERSON_V_3_URL" to "person.v3.url",
                "PDL_API_URL" to "PDL_API_URL",
                "PDL_API_APIKEY" to "PDL_API_APIKEY",
                "STS_APIKEY" to "STS_APIKEY",
                "STS_TOKENENDPOINT_URL" to "STS_TOKENENDPOINT_URL",
                "SECURITYTOKENSERVICE_URL" to "SECURITYTOKENSERVICE_URL",
                "FSS_SRVMININNBOKS_USERNAME" to "FSS_SRVMININNBOKS_USERNAME",
                "FSS_SRVMININNBOKS_PASSWORD" to "FSS_SRVMININNBOKS_PASSWORD",
                "SRVMININNBOKS_USERNAME" to "SRVMININNBOKS_USERNAME",
                "SRVMININNBOKS_PASSWORD" to "SRVMININNBOKS_PASSWORD",
                "LOGINSERVICE_IDPORTEN_AUDIENCE" to "LOGINSERVICE_IDPORTEN_AUDIENCE"
        )
)

data class Configuration(
        val jwksUrl: JwkProvider = JwtUtil.makeJwkProvider(config()[Key("JWKS_URL", stringType)]),
        val jwtIssuer: String = config()[Key("ISSUER", stringType)],
        val SERVICEGATEWAY_URL: String = config()[Key("SERVICEGATEWAY_URL", stringType)],
        val INNSYN_HENVENDELSE_WS_URL: String = config()[Key("SERVICEGATEWAY_URL", stringType)],
        val HENVENDELSE_WS_URL: String = config()[Key("SERVICEGATEWAY_URL", stringType)],
        val SEND_INN_HENVENDELSE_WS_URL: String = config()[Key("SERVICEGATEWAY_URL", stringType)],
        val PERSON_V_3_URL: String = config()[Key("SERVICEGATEWAY_URL", stringType)],
        val PDL_API_URL: String = config()[Key("PDL_API_URL", stringType)],
        val PDL_API_APIKEY: String = config()[Key("PDL_API_APIKEY", stringType)],
        val STS_APIKEY: String = config()[Key("STS_APIKEY", stringType)],
        val STS_TOKENENDPOINT_URL: String = config()[Key("STS_TOKENENDPOINT_URL", stringType)],
        val SECURITYTOKENSERVICE_URL: String = config()[Key("SECURITYTOKENSERVICE_URL", stringType)],
        val FSS_SRVMININNBOKS_USERNAME: String = config()[Key("FSS_SRVMININNBOKS_USERNAME", stringType)],
        val FSS_SRVMININNBOKS_PASSWORD: String = config()[Key("FSS_SRVMININNBOKS_PASSWORD", stringType)],
        val SRVMININNBOKS_USERNAME: String = config()[Key("SRVMININNBOKS_USERNAME", stringType)],
        val LOGINSERVICE_IDPORTEN_DISCOVERY_URL: String = config()[Key("LOGINSERVICE_IDPORTEN_DISCOVERY_URL", stringType)],
        val LOGINSERVICE_IDPORTEN_AUDIENCE: String = config()[Key("LOGINSERVICE_IDPORTEN_AUDIENCE", stringType)],
        val SRVMININNBOKS_PASSWORD: String = config()[Key("SRVMININNBOKS_PASSWORD", stringType)]

)

private fun config() = ConfigurationProperties.systemProperties() overriding
        EnvironmentVariables overriding
        defaultProperties
