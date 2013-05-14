package no.nav.sbl.dialogarena.minehenvendelser.selftest;

import no.nav.modig.core.context.Principal;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.nav.modig.core.context.SecurityContext.getCurrent;

public class SelfTestPage extends WebPage {

    private static final String CMS_OK = "UNI_CMS_CONTENT_RETRIEVER_OK";
    private static final String CMS_ERROR = "UNI_CMS_CONTENT_RETRIEVER_ERROR";
    private static final String HENVENDELSE_OK = "UNI_HENVENDELSECONSUMER_OK";
    private static final String HENVENDELSE_ERROR = "UNI_HENVENDELSECONSUMER_ERROR";

    @Inject
    private CmsContentRetriever cmsContentRetriever;

    @Inject
    private HenvendelsesBehandlingPortType henvendelsesBehandlingService;

    private static final Logger logger = LoggerFactory.getLogger(SelfTestPage.class);

    public SelfTestPage() throws IOException {
        logger.info("entered SelfTestPage!");
        List<ServiceStatus> statusList = new ArrayList<>();
        statusList.add(getCmsStatus());
        statusList.add(getHenvendelseWSStatus());
        add(
                new ServiceStatusListView("serviceStatusTable", statusList),
                new Label("cmsinfo", "Cms-server: " + cmsContentRetriever.getCmsIp()),
                getCmsKeys(),
                new Label("application", getApplicationVersion()));
    }

    private ServiceStatus getCmsStatus() {
        long start = currentTimeMillis();
        int statusCode = 0;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(cmsContentRetriever.getCmsIp()).openConnection();
            connection.setConnectTimeout(10000);
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            logger.warn("Cms not reachable on " + cmsContentRetriever.getCmsIp());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        String status = statusCode == HTTP_OK ? CMS_OK : CMS_ERROR;
        return new ServiceStatus(format("Enonic CMS (%s)", cmsContentRetriever.getCmsIp()), status, currentTimeMillis() - start);
    }

    private ServiceStatus getHenvendelseWSStatus() {
        getCurrent().setPrincipal(new Principal.Builder()
                .userId("12121211111")
                .authenticationLevel("4")
                .consumerId("minehenvendelser")
                .identType("eksternBruker")
                .build());
        long start = currentTimeMillis();
        boolean available = henvendelsesBehandlingService.ping();
        String status = available ? HENVENDELSE_OK : HENVENDELSE_ERROR;
        return new ServiceStatus("Henvendelse WS", status, currentTimeMillis() - start);
    }

    private CmsStatusListView getCmsKeys() {
        String[] keys = {"topp.tekst", "slutt.tekst" };
        List<CmsStatus> cmsStatusList = new ArrayList<>();
        for (String key : keys) {
            cmsStatusList.add(new CmsStatus(key));
        }
        return new CmsStatusListView("cmsStatusTable", cmsStatusList);
    }

    private String getApplicationVersion() throws IOException {
        String version;
        WebRequest req = (WebRequest) RequestCycle.get().getRequest();
        ServletContext servletContext = ((HttpServletRequest) req.getContainerRequest()).getServletContext();
        InputStream inputStream = servletContext.getResourceAsStream(("/META-INF/MANIFEST.MF"));
        if (inputStream != null) {
            Manifest manifest = new Manifest(inputStream);
            version = manifest.getMainAttributes().getValue("Implementation-Version");
        } else {
            version = "cannot locate manifest, version unknown";
        }
        return "minehenvendelser - " + version;
    }

    private static class ServiceStatusListView extends PropertyListView<ServiceStatus> {

        private static final long serialVersionUID = 1L;

        public ServiceStatusListView(String id, List<ServiceStatus> statusList) {
            super(id, statusList);
        }

        @Override
        protected void populateItem(ListItem<ServiceStatus> listItem) {
            listItem.add(new Label("name"), new Label("status"), new Label("durationMilis"));
        }
    }

    private static class ServiceStatus implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String name;
        private final String status;
        private final long durationMilis;

        public ServiceStatus(String name, String status, long durationMilis) {
            this.name = name;
            this.status = status;
            this.durationMilis = durationMilis;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public long getDurationMilis() {
            return durationMilis;
        }

    }

    private static class CmsStatusListView extends PropertyListView<CmsStatus> {

        private static final long serialVersionUID = 1L;

        public CmsStatusListView(String id, List<CmsStatus> cmsStatusList) {
            super(id, cmsStatusList);
        }

        @Override
        protected void populateItem(ListItem<CmsStatus> listItem) {
            Label valueLabel = new Label("value");
            valueLabel.setEscapeModelStrings(true);
            listItem.add(new Label("key"), valueLabel);
        }
    }

    private class CmsStatus implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String key;
        private final String value;

        public CmsStatus(String key) {
            this.key = key;
            this.value = cmsContentRetriever.hentTekst(key);
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}
