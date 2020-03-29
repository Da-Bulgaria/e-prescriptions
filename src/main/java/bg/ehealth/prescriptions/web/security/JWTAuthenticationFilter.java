package bg.ehealth.prescriptions.web.security;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import bg.ehealth.prescriptions.services.UserService;

/**
 * Filter that handles JWT authentication.
 *
 * Note: we use JWT only as a encrypted-and-signed userID cookie. That is needed to avoid having to share
 * session state across instances (user is loaded on each request from the database or cache)
 */
public class JWTAuthenticationFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private String jwtSecret;
    private List<String> unauthenticatedUris;
    private UserService userService;
    
    public JWTAuthenticationFilter(List<String> unauthenticatedUris, String jwtSecret, UserService userService) {
        this.jwtSecret = jwtSecret;
        this.unauthenticatedUris = unauthenticatedUris;
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestedUri = httpServletRequest.getRequestURI();

        for (String ignored : unauthenticatedUris) {
            if (requestedUri.toLowerCase().startsWith(ignored.toLowerCase())) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        LoginAuthenticationToken authentication =
                TokenAuthenticationService.getAuthentication(httpServletRequest, httpServletResponse, 
                        jwtSecret, userService);
        if (authentication != null) {
            if (authentication.getUser() == null) {
                // user does not exist, so we just proceed with the filter without setting it in the security context;
                logger.warn("User passed in token not found: " + authentication.getName());
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else if (WebUtils.isAjax(httpServletRequest)) {
            // this is not actual security, just convenience for better user experience
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");
            return;
        } else {
            // we need to clear the authentication which is cached in the session to prevent CSRF
            SecurityContextHolder.getContext().setAuthentication(null);
            // unauthenticated non-get and non-head requests should be blocked as they may be CSRF attempts
            if (!"GET".equals(httpServletRequest.getMethod())
                    && !"HEAD".equals((httpServletRequest).getMethod())) {
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}