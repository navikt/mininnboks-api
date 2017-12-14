package no.nav.sbl.dialogarena.mininnboks.provider.filter;

import no.nav.modig.core.exception.AuthorizationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.mininnboks.provider.sikkerhet.XsrfUtils.sjekkXsrfToken;
import static no.nav.sbl.dialogarena.mininnboks.provider.sikkerhet.XsrfUtils.xsrfCookie;

public class XsrfFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        switch (httpRequest.getMethod()) {
            case "GET":
                httpResponse.addCookie(xsrfCookie(getFnr(), httpRequest.getSession()));
                chain.doFilter(request, response);
                break;
            case "POST":
                sjekkToken(request, response, chain, httpRequest, httpResponse);
                break;
            default:
                httpResponse.sendError(SC_METHOD_NOT_ALLOWED);
        }
    }

    private void sjekkToken(ServletRequest request, ServletResponse response, FilterChain chain,
                            HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws IOException, ServletException {

        try {
            sjekkXsrfToken(httpRequest.getHeader("X-XSRF-TOKEN"), getFnr());
            chain.doFilter(request, response);
        } catch (AuthorizationException e) {
            httpResponse.sendError(SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private String getFnr(){
        return getSubjectHandler().getUid();
    }

    @Override
    public void destroy() {
    }
}
