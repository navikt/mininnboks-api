package no.nav.sbl.dialogarena.minehenvendelser.selftest;

import no.nav.modig.wicket.selftest.SelfTestBase;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmal.v1.SporsmalinnsendingPortType;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.BehandleBrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Viser status for alle integrasjonspunkter
 */
public class SelfTestPage extends SelfTestBase {

    private static final Logger logger = LoggerFactory.getLogger(SelfTestPage.class);

    @Inject
    @Named("henvendelsesSystemUser")
    private HenvendelseMeldingerPortType henvendelsesSystemUser;

    @Inject
    @Named("sporsmalinnsendingSystemUser")
    private SporsmalinnsendingPortType sporsmalinnsendingSystemUser;

    @Inject
    @Named("brukerprofilSystemUser")
    private BrukerprofilPortType brukerprofilSystemUser;

    @Inject
    @Named("behandleBrukerprofilSystemUser")
    private BehandleBrukerprofilPortType behandleBrukerprofilSystemUser;

    public SelfTestPage(PageParameters params) throws IOException {
        super("Minehenvendelser", params);
    }

    private AvhengighetStatus getCmsStatus() {
        String cmsBaseUrl = System.getProperty("dialogarena.cms.url");
        long start = currentTimeMillis();
        int statusCode = 0;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(cmsBaseUrl).openConnection();
            connection.setConnectTimeout(10000);
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            logger.warn("Cms not reachable on " + cmsBaseUrl);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        String status = HTTP_OK == statusCode ? SelfTestBase.STATUS_OK : SelfTestBase.STATUS_ERROR;
        return new AvhengighetStatus("ENONIC_CMS", status, currentTimeMillis() - start, format("URL: %s", cmsBaseUrl));
    }

    private AvhengighetStatus getHenvendelseWSStatus() {
        long start = currentTimeMillis();
        String status = SelfTestBase.STATUS_ERROR;
        try {
            henvendelsesSystemUser.ping();
            status = SelfTestBase.STATUS_OK;
        } catch (Exception e) {
            logger.warn("<<<<<<Error Contacting Henvendelse WS: " + e.getMessage(), e);
        }
        return new AvhengighetStatus("HENVENDELSE_TJENESTE_PING", status, currentTimeMillis() - start);
    }

    private AvhengighetStatus getSporsmalinnsendingWSStatus() {
        long start = currentTimeMillis();
        String status = SelfTestBase.STATUS_ERROR;
        try {
            if (sporsmalinnsendingSystemUser.ping()) {
                status = SelfTestBase.STATUS_OK;
            }
        } catch (Exception e) {
            logger.warn("<<<<<<Error Contacting Sporsmalinnsending WS: " + e.getMessage(), e);
        }

        return new AvhengighetStatus("SPORSMAL_TJENESTE_PING", status, currentTimeMillis() - start);
    }

    private AvhengighetStatus getBrukerprofilWSStatus() {
        long start = currentTimeMillis();
        String status = SelfTestBase.STATUS_ERROR;
        try {
            brukerprofilSystemUser.ping();
            status = SelfTestBase.STATUS_OK;
        } catch (Exception e) {
            logger.warn("<<<<<<Error Contacting Brukerprofil WS: " + e.getMessage(), e);
        }
        return new AvhengighetStatus("BRUKERPROFIL_TJENESTE_PING", status, currentTimeMillis() - start);
    }

    private AvhengighetStatus getBehandleBrukerprofilWSStatus() {
        long start = currentTimeMillis();
        String status = SelfTestBase.STATUS_ERROR;
        try {
            behandleBrukerprofilSystemUser.ping();
            status = SelfTestBase.STATUS_OK;
        } catch (Exception e) {
            logger.warn("<<<<<<Error Contacting BehandleBrukerProfil WS: " + e.getMessage(), e);
        }
        return new AvhengighetStatus("BEHANDLE_BRUKERPROFIL_TJENESTE_PING", status, currentTimeMillis() - start);
    }

    @Override
    protected void addToStatusList(List<AvhengighetStatus> statusList) {
        statusList.add(getHenvendelseWSStatus());
        statusList.add(getSporsmalinnsendingWSStatus());
        statusList.add(getBrukerprofilWSStatus());
        statusList.add(getBehandleBrukerprofilWSStatus());
        statusList.add(getCmsStatus());
    }
}