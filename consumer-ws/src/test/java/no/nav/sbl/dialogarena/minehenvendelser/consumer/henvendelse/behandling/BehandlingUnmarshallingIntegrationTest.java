package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.config.ApplicationContextConsumer;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContextConsumer.class})
public class BehandlingUnmarshallingIntegrationTest {

    @Inject
    private Jaxb2Marshaller jaxb2Marshaller;

    @Test
    public void shouldUnmarshall() {
        InputStream inputStream = getClass().getResourceAsStream("/xsd/behandlingsinformasjon.xsd.xml");
        Behandlinger behandlinger = (Behandlinger) jaxb2Marshaller.unmarshal(new StreamSource(inputStream));

        Assert.assertThat(behandlinger.getBehandlingDTOs().get(0).getBehandlingstype(), equalTo("type-ETTERINNSENDING"));
        Assert.assertThat(behandlinger.getBehandlingDTOs().get(0).getStatus(), equalTo("STATUS-FERDIG"));
        Assert.assertThat(behandlinger.getBehandlingDTOs().get(0).getHovedkravskjemaId(), equalTo("hid-DAGP"));
        Assert.assertThat(behandlinger.getBehandlingDTOs().get(0).getBehandlingstype(), equalTo("type-ETTERINNSENDING"));
        Assert.assertThat(behandlinger.getBehandlingDTOs().get(0).getSistEndret(), equalTo(new DateTime("2008-09-29T03:49:45")));
        Assert.assertThat(behandlinger.getBehandlingDTOs().get(0).getInnsendtDato(), equalTo(new DateTime("2014-09-19T01:18:33")));


    }

}
