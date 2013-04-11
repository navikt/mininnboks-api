package no.nav.sbl.dialogarena.minehenvendelser.selftest;

import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;

public class SelfTestPage extends WebPage {

    private static final Logger logger = LoggerFactory.getLogger(SelfTestPage.class);

    public SelfTestPage() throws IOException {
        logger.info("entered SelfTestPage!");
        String version = getApplicationVersion();
        String startUpDate = new Date(WicketApplication.get().getApplicationContext().getStartupDate()).toString();
        List<ServiceStatus> statusList = new ArrayList<>();
        statusList.add(new ServiceStatus("Testet Appcontext", "OK, startet opp: " + startUpDate, 0));
        statusList.add(new ServiceStatus("Applikasjonsversjon", version, 0));
        add(new ServiceStatusListView("serviceStatusTable", statusList));
    }

    private String getApplicationVersion() throws IOException {
        String version = "unknown version";
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

}
