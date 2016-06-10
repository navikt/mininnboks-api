package no.nav.sbl.dialogarena.mininnboks.provider.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getRequestURI().equals("/mininnboks/")) {
            httpResponse.sendRedirect("/mininnboks/app");
            return;
        }
        filterChain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {}
}
