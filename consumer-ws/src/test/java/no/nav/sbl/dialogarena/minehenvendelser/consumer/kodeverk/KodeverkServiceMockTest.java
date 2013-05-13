package no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class KodeverkServiceMockTest {

    private KodeverkService service;

    @Before
    public void setup() {
        service = new KodeverkServiceMock();

    }

    @Test
    public void shouldContainDefaultValues() {
        ((KodeverkServiceMock) service).createMockKodeverk();
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_1), equalTo("Søknad om dagpenger"));
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_2), equalTo("Permitteringsvarsel"));
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_3), equalTo("Arbeidsavtale"));
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_4), equalTo("Annet: "));
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_5), equalTo("Søknad om foreldrepenger"));
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_6), equalTo("Inntektsopplysninger"));
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_7), equalTo("Søknad om kontantstøtte"));
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_8), equalTo("Stønad om Overgangsstønad"));
        assertThat(service.hentKodeverk(KodeverkServiceMock.KODEVERK_ID_9), equalTo("Avtale om delt bosted"));
    }

    @Test
    public void shouldReportMissingKodeverk() {
        assertThat(service.hentKodeverk("missing"), equalTo("Kodeverk mangler: missing"));
    }

    @Test
    public void shouldReportEgenDefKode() {
        assertThat(service.isEgendefKode(KodeverkServiceMock.KODEVERK_ID_4), equalTo(true));
    }


}

