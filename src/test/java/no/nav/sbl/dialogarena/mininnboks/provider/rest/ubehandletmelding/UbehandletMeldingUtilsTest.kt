package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding;

import no.nav.sbl.dialogarena.mininnboks.config.LinkMock;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import org.junit.Test;

import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.mininnboks.TestUtils.*;
import static no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.UbehandletMelding.Status.UBESVART;
import static no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.UbehandletMelding.Status.ULEST;
import static no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.UbehandletMeldingUtils.hentUbehandledeMeldinger;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UbehandletMeldingUtilsTest {

    static {
        LinkMock.setup();
    }

    @Test
    public void asdf(){
        ZonedDateTime parse = ZonedDateTime.parse("2019-01-18T14:51:10.302528Z");
        System.out.println(parse.toEpochSecond());
    }

    @Test
    public void delerOppHenvendelserSporsmalsVarselPerTraad() {
        Henvendelse traad1henvendelse1 = lagHenvendelse("1", "1");
        Henvendelse traad1henvendelse2 = lagHenvendelse("2", traad1henvendelse1.id);
        Henvendelse traad2henvendelse1 = lagHenvendelse("3", "3");
        List<Henvendelse> henvendelser = new ArrayList<>(asList(traad1henvendelse1, traad1henvendelse2, traad2henvendelse1));

        List<UbehandletMelding> sporsmalVarsler = hentUbehandledeMeldinger(henvendelser);

        assertThat(sporsmalVarsler.size(), is(2));
    }

    @Test
    public void hentKunNyesteHenvendelseITraad() {
        Date nyesteDato = nowPlus(-1);
        Henvendelse traad1henvendelse1 = lagHenvendelse("1", "1", nyesteDato);
        Henvendelse traad1henvendelse2 = lagHenvendelse("2", "1", nowPlus(-100));
        List<Henvendelse> henvendelser = new ArrayList<>(asList(traad1henvendelse1, traad1henvendelse2));

        List<UbehandletMelding> sporsmalVarsler = hentUbehandledeMeldinger(henvendelser);

        assertThat(sporsmalVarsler.size(), is(1));
        assertThat(sporsmalVarsler.get(0).opprettetDato, is(nyesteDato));
    }

    @Test
    public void henterUtUlesteMeldinger() {
        Henvendelse ulestIkkeUbesvartHenvendelse = lagHenvendelse(false);
        Henvendelse lestIkkeUbesvartHenvendelse = lagHenvendelse(true);

        List<UbehandletMelding> sporsmalVarsler = hentUbehandledeMeldinger(asList(ulestIkkeUbesvartHenvendelse, lestIkkeUbesvartHenvendelse));

        assertThat(sporsmalVarsler.size(), is(1));
        assertThat(sporsmalVarsler.get(0).behandlingskjedeId, is(ulestIkkeUbesvartHenvendelse.traadId));
        assertThat(sporsmalVarsler.get(0).statuser.get(0), is(ULEST));
    }

    @Test
    public void henterUtUbesvarteSporsmaal() {
        Henvendelse ubesvartLestHenvendelse = lagForsteHenvendelseITraad(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE, true);
        Henvendelse besvartLestHenvendelse = lagForsteHenvendelseITraad(Henvendelsetype.SVAR_SKRIFTLIG, true);

        List<UbehandletMelding> sporsmalVarsler = hentUbehandledeMeldinger(asList(ubesvartLestHenvendelse, besvartLestHenvendelse));

        assertThat(sporsmalVarsler.size(), is(1));
        assertThat(sporsmalVarsler.get(0).behandlingskjedeId, is(ubesvartLestHenvendelse.traadId));
        assertThat(sporsmalVarsler.get(0).statuser.get(0), is(UBESVART));
    }

    @Test
    public void lagerKunEttVarselForEtUlestUbesvartSporsmaal() {
        Henvendelse ubesvartUlestHenvendelse = lagForsteHenvendelseITraad(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE, false);

        List<UbehandletMelding> sporsmalVarsler = hentUbehandledeMeldinger(asList(ubesvartUlestHenvendelse));

        assertThat(sporsmalVarsler.size(), is(1));
        assertThat(sporsmalVarsler.get(0).statuser.size(), is(2));
        assertThat(sporsmalVarsler.get(0).statuser.contains(UBESVART), is(true));
        assertThat(sporsmalVarsler.get(0).statuser.contains(ULEST), is(true));
    }
}