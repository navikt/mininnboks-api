package no.nav.sbl.dialogarena.mininnboks.security;

import no.nav.modig.core.context.SubjectHandlerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SecurityFilterMock implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityFilterMock.class);

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.warn("Aktivert " + getClass().getSimpleName() + "! Skal ikke opptre i produksjon!");
    }

    // Checkstyle tror det er redundante Exceptions
    // CHECKSTYLE:OFF
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if (req.getRequestURI().matches("^(.*internal/selftest.*)|(.*index.html)|(.*feil.*)|((.*)\\.(js|css|jpg))")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (req.getParameter("fnr") != null) {
            req.getSession().setAttribute("fnr", req.getParameter("fnr"));
        }
        String fnr = (String) req.getSession().getAttribute("fnr");
        if (fnr == null) {
            fnr = "10108000398";
        }

        SubjectHandlerUtils.setEksternBruker(fnr, 4, null);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

}
