package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandlinger;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerContext.class})
public class BehandlingUnmarshallingIntegrationTest {

    @Inject
    private Jaxb2Marshaller jaxb2Marshaller;

    @Test
    public void shouldUnmarshall() {
        InputStream inputStream = getClass().getResourceAsStream("/xsd/behandlingsinformasjon.xsd.xml");
        Behandlinger behandlinger = (Behandlinger) jaxb2Marshaller.unmarshal(new StreamSource(inputStream));

        assertThat(getFirstBehandlingFromList(behandlinger).getBehandlingstype(), equalTo("type-ETTERINNSENDING"));
        assertThat(getFirstBehandlingFromList(behandlinger).getStatus(), equalTo("STATUS-FERDIG"));
        assertThat(getFirstBehandlingFromList(behandlinger).getHovedkravskjemaId(), equalTo("hid-DAGP"));
        assertThat(getFirstBehandlingFromList(behandlinger).getBehandlingstype(), equalTo("type-ETTERINNSENDING"));
        assertThat(getFirstBehandlingFromList(behandlinger).getSistEndret(), equalTo(new DateTime("2008-09-29T03:49:45")));
        assertThat(getFirstBehandlingFromList(behandlinger).getInnsendtDato(), equalTo(new DateTime("2014-09-19T01:18:33")));

        assertThat(behandlinger.getBehandlingerList().size(), equalTo(1));
        assertThat(getFirstBehandlingFromList(behandlinger).getDokumentforventninger().size(), equalTo(1));

        assertThat(getFirstBehandlingFromList(behandlinger).getDokumentforventninger().get(0).getSkjemaId(), equalTo("skjemaID-LEGE"));
        assertThat(getFirstBehandlingFromList(behandlinger).getDokumentforventninger().get(0).getEgendefinertTittel(), equalTo("tittel-CV"));
        assertThat(getFirstBehandlingFromList(behandlinger).getDokumentforventninger().get(0).getInnsendingsvalg(), equalTo("type-ELEK"));
        assertThat(getFirstBehandlingFromList(behandlinger).getDokumentforventninger().get(0).isHovedskjema(), equalTo(true));
    }

    private Behandling getFirstBehandlingFromList(Behandlinger behandlinger) {
        return behandlinger.getBehandlingerList().get(0);
    }

}
