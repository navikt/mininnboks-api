package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.BehandlingerResponse;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.Innsendingsvalg.SENDES_AV_ANDRE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerContext.class})
public class BehandlingUnmarshallingIntegrationTest {

    @Inject
    private Jaxb2Marshaller jaxb2Marshaller;

    @Test
    public void shouldUnmarshall() {
        InputStream inputStream = getClass().getResourceAsStream("/xsd/henvendelse.xsd.xml");
        BehandlingerResponse behandlingerResponse = (BehandlingerResponse) jaxb2Marshaller.unmarshal(new StreamSource(inputStream));

        Behandling forsteBehandling = behandlingerResponse.getBehandlinger().get(0);

        assertThat(forsteBehandling.getBehandlingstype(), equalTo("DOKUMENT_BEHANDLING"));
        assertThat(forsteBehandling.getStatus(), equalTo("FERDIG"));
        assertThat(forsteBehandling.getBehandlingstype(), equalTo("DOKUMENT_BEHANDLING"));
        assertThat(forsteBehandling.getSistEndret(), equalTo(new DateTime("2014-09-19T01:18:33+02:00")));
        assertThat(forsteBehandling.getInnsendtDato(), equalTo(new DateTime("2006-08-19T19:27:14+02:00")));

        assertThat(behandlingerResponse.getBehandlinger().size(), equalTo(1));
        assertThat(forsteBehandling.getDokumentforventninger().size(), equalTo(1));

        assertThat(forsteBehandling.getDokumentforventninger().get(0).getKodeverkId(), equalTo("kodeverk33"));
        assertThat(forsteBehandling.getDokumentforventninger().get(0).getInnsendingsvalg(), equalTo(SENDES_AV_ANDRE));
        assertThat(forsteBehandling.getDokumentforventninger().get(0).isHovedskjema(), equalTo(true));
        assertThat(forsteBehandling.getDokumentforventninger().get(0).getEgendefinertTittel(), equalTo("CV"));
    }
}
