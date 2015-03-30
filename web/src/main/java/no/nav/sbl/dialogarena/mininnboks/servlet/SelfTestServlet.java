package no.nav.sbl.dialogarena.mininnboks.servlet;

import no.nav.sbl.dialogarena.common.web.selftest.SelfTestBaseServlet;
import no.nav.sbl.dialogarena.types.Pingable;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import java.util.Collection;

public class SelfTestServlet extends SelfTestBaseServlet {

    private ApplicationContext ctx = null;

    @Override
    public void init() throws ServletException {
        ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        super.init();
    }

    @Override
    protected Collection<Pingable> getPingables() {
        return ctx.getBeansOfType(Pingable.class).values();
    }

    @Override
    protected String getApplicationName() {
        return "Mininnboks";
    }

}