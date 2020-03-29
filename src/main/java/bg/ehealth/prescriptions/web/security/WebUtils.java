package bg.ehealth.prescriptions.web.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Collection of web-related utility methods
 */
public class WebUtils {

    public static String getRequestIp(HttpServletRequest httpRequest) {
        // Account for cases when we are behind load-balancer
        String remoteIp = httpRequest.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(remoteIp)) {
            remoteIp = httpRequest.getRemoteAddr();
        }
        return remoteIp.split(",")[0];
    }

    public static String getRequestIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return getRequestIp(request);
        }
        return null;
    }

    public static Map<String, String> getCurrentRequestHeaders() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return getRequestHeadersMap(request);
        }
        return Collections.emptyMap();
    }

    public static String getCurrentRequestURL() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getRequestURL().toString();
        }

        return null;
    }

    private static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    private static Map<String, String> getRequestHeadersMap(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
