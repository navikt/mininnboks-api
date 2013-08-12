package no.nav.sbl.dialogarena.minehenvendelser;

import org.slf4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static no.nav.modig.core.context.SubjectHandlerUtils.setEksternBruker;
import static no.nav.sbl.dialogarena.minehenvendelser.config.MockApplicationContext.AKTOR_ID;
import static org.slf4j.LoggerFactory.getLogger;

public class SecurityFilterMock implements Filter {

    private static final Logger LOG = getLogger(SecurityFilterMock.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.warn("Aktivert " + getClass().getSimpleName() + "! Skal ikke opptre i produksjon!");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        setEksternBruker(AKTOR_ID, 4, null);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

}
