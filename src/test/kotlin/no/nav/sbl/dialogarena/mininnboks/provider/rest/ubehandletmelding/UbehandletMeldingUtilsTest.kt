package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.provider.LinkService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.spekframework.spek2.Spek

class UbehandletMeldingUtilsTest : Spek({
    setup()

    test("deler Opp Henvendelser SporsmalsVarsel Per Traad") {
        val traad1henvendelse1 = TestUtils.lagHenvendelse("1", "1")
        val traad1henvendelse2 = TestUtils.lagHenvendelse("2", traad1henvendelse1.id)
        val traad2henvendelse1 = TestUtils.lagHenvendelse("3", "3")
        val henvendelser: List<Henvendelse> =
            ArrayList(listOf(traad1henvendelse1, traad1henvendelse2, traad2henvendelse1))
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(henvendelser)
        assertThat(sporsmalVarsler.size, Matchers.`is`(2))
    }

    test("hent Kun Nyeste Henvendelse I Traad") {
        val nyesteDato = TestUtils.nowPlus(-1)
        val traad1henvendelse1 = TestUtils.lagHenvendelse("1", "1", nyesteDato)
        val traad1henvendelse2 = TestUtils.lagHenvendelse("2", "1", TestUtils.nowPlus(-100))
        val henvendelser: List<Henvendelse> = ArrayList(listOf(traad1henvendelse1, traad1henvendelse2))
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(henvendelser)
        assertThat(sporsmalVarsler.size, Matchers.`is`(1))
        assertThat(sporsmalVarsler[0].opprettetDato, Matchers.`is`(nyesteDato))
    }

    test("henter Ut Uleste Meldinger") {
        val ulestIkkeUbesvartHenvendelse = TestUtils.lagHenvendelse(false)
        val lestIkkeUbesvartHenvendelse = TestUtils.lagHenvendelse(true)
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(
            listOf(
                ulestIkkeUbesvartHenvendelse,
                lestIkkeUbesvartHenvendelse
            )
        )
        assertThat(sporsmalVarsler.size, Matchers.`is`(1))
        assertThat(sporsmalVarsler[0].behandlingskjedeId, Matchers.`is`(ulestIkkeUbesvartHenvendelse.traadId))
        assertThat(sporsmalVarsler[0].statuser[0], Matchers.`is`(Status.ULEST))
    }

    test("henter Ut Ubesvarte Sporsmaal") {
        val ubesvartLestHenvendelse =
            TestUtils.lagForsteHenvendelseITraad(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE, true)
        val besvartLestHenvendelse = TestUtils.lagForsteHenvendelseITraad(Henvendelsetype.SVAR_SKRIFTLIG, true)
        val sporsmalVarsler =
            UbehandletMeldingUtils.hentUbehandledeMeldinger(listOf(ubesvartLestHenvendelse, besvartLestHenvendelse))
        assertThat(sporsmalVarsler.size, Matchers.`is`(1))
        assertThat(sporsmalVarsler[0].behandlingskjedeId, Matchers.`is`(ubesvartLestHenvendelse.traadId))
        assertThat(sporsmalVarsler[0].statuser[0], Matchers.`is`(Status.UBESVART))
    }

    test("lager Kun Ett Varsel For Et Ulest Ubesvart Sporsmaal") {
        val ubesvartUlestHenvendelse =
            TestUtils.lagForsteHenvendelseITraad(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE, false)
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(listOf(ubesvartUlestHenvendelse))
        assertThat(sporsmalVarsler.size, Matchers.`is`(1))
        assertThat(sporsmalVarsler[0].statuser.size, Matchers.`is`(2))
        assertThat(sporsmalVarsler[0].statuser.contains(Status.UBESVART), Matchers.`is`(true))
        assertThat(sporsmalVarsler[0].statuser.contains(Status.ULEST), Matchers.`is`(true))
    }
})

fun setup() {
    EnvironmentUtils.setProperty(LinkService.MININNBOKS_LINK_PROPERTY, "/", EnvironmentUtils.Type.PUBLIC)
    EnvironmentUtils.setProperty(LinkService.SAKSOVERSIKT_LINK_PROPERTY, "/", EnvironmentUtils.Type.PUBLIC)
    EnvironmentUtils.setProperty(LinkService.TEMAVELGER_LINK_PROPERTY, "/", EnvironmentUtils.Type.PUBLIC)
    EnvironmentUtils.setProperty(LinkService.BRUKERPROFIL_LINK_PROPERTY, "/", EnvironmentUtils.Type.PUBLIC)
}
