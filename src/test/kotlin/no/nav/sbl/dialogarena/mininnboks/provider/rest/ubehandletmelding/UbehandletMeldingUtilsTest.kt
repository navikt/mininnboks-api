package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.sbl.dialogarena.mininnboks.config.LinkMock.setup
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import java.util.*

class UbehandletMeldingUtilsTest {
    companion object {
        init {
            setup()
        }
    }

    @Test
    fun delerOppHenvendelserSporsmalsVarselPerTraad() {
        val traad1henvendelse1 = TestUtils.lagHenvendelse("1", "1")
        val traad1henvendelse2 = TestUtils.lagHenvendelse("2", traad1henvendelse1.id)
        val traad2henvendelse1 = TestUtils.lagHenvendelse("3", "3")
        val henvendelser: List<Henvendelse> = ArrayList(listOf(traad1henvendelse1, traad1henvendelse2, traad2henvendelse1))
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(henvendelser)
        Assert.assertThat(sporsmalVarsler.size, Matchers.`is`(2))
    }

    @Test
    fun hentKunNyesteHenvendelseITraad() {
        val nyesteDato = TestUtils.nowPlus(-1)
        val traad1henvendelse1 = TestUtils.lagHenvendelse("1", "1", nyesteDato)
        val traad1henvendelse2 = TestUtils.lagHenvendelse("2", "1", TestUtils.nowPlus(-100))
        val henvendelser: List<Henvendelse> = ArrayList(listOf(traad1henvendelse1, traad1henvendelse2))
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(henvendelser)
        Assert.assertThat(sporsmalVarsler.size, Matchers.`is`(1))
        Assert.assertThat(sporsmalVarsler[0].opprettetDato, Matchers.`is`(nyesteDato))
    }

    @Test
    fun henterUtUlesteMeldinger() {
        val ulestIkkeUbesvartHenvendelse = TestUtils.lagHenvendelse(false)
        val lestIkkeUbesvartHenvendelse = TestUtils.lagHenvendelse(true)
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(listOf(ulestIkkeUbesvartHenvendelse, lestIkkeUbesvartHenvendelse))
        Assert.assertThat(sporsmalVarsler.size, Matchers.`is`(1))
        Assert.assertThat(sporsmalVarsler[0].behandlingskjedeId, Matchers.`is`(ulestIkkeUbesvartHenvendelse.traadId))
        Assert.assertThat(sporsmalVarsler[0].statuser[0], Matchers.`is`(UbehandletMelding.Status.ULEST))
    }

    @Test
    fun henterUtUbesvarteSporsmaal() {
        val ubesvartLestHenvendelse = TestUtils.lagForsteHenvendelseITraad(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE, true)
        val besvartLestHenvendelse = TestUtils.lagForsteHenvendelseITraad(Henvendelsetype.SVAR_SKRIFTLIG, true)
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(listOf(ubesvartLestHenvendelse, besvartLestHenvendelse))
        Assert.assertThat(sporsmalVarsler.size, Matchers.`is`(1))
        Assert.assertThat(sporsmalVarsler[0].behandlingskjedeId, Matchers.`is`(ubesvartLestHenvendelse.traadId))
        Assert.assertThat(sporsmalVarsler[0].statuser[0], Matchers.`is`(UbehandletMelding.Status.UBESVART))
    }

    @Test
    fun lagerKunEttVarselForEtUlestUbesvartSporsmaal() {
        val ubesvartUlestHenvendelse = TestUtils.lagForsteHenvendelseITraad(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE, false)
        val sporsmalVarsler = UbehandletMeldingUtils.hentUbehandledeMeldinger(listOf(ubesvartUlestHenvendelse))
        Assert.assertThat(sporsmalVarsler.size, Matchers.`is`(1))
        Assert.assertThat(sporsmalVarsler[0].statuser.size, Matchers.`is`(2))
        Assert.assertThat(sporsmalVarsler[0].statuser.contains(UbehandletMelding.Status.UBESVART), Matchers.`is`(true))
        Assert.assertThat(sporsmalVarsler[0].statuser.contains(UbehandletMelding.Status.ULEST), Matchers.`is`(true))
    }
}
