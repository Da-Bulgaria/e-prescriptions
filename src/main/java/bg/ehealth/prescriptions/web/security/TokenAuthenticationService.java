package bg.ehealth.prescriptions.web.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import bg.ehealth.prescriptions.persistence.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.servlet.spec.HttpServletResponseImpl;

/**
 * Service that handles authentication using JSON web tokens (used by the back-office application)
 */
@Component
public class TokenAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token_trails";
    private static final long EXPIRATION_TIME = 432_000_000; // 5 days
    private static final String TOKEN_PREFIX = "Bearer";
    private static final int MILLIS_PER_SECOND = 1000;
    public static final String HASH_CLAIM = "hash";


    /**
     * Adds a JWT authentication to the session
     *
     * @param res
     * @param user
     * @param secureCookies
     * @param secret
     */


    /**
     * Adds a JWT authentication to the session
     *
     * @param res
     * @param user
     * @param secureCookies
     * @param secret
     */


    public static void addAuthentication(HttpServletRequest req, HttpServletResponse res,
                                         User user, boolean secureCookies, String secret) {
        String jwt = createJwtToken(user.getId(), secret,
                    getHashClaim(user.getEmail(), user.getTwoFactorAuthSecret() != null, user.getPassword()));

        setCookie(res, jwt, secureCookies);
    }

    public static String getHashClaim(Object... args) {
        // note: the password is transformed with scrypt
        // hashing twice to avoid the risk of a very targeted brute force
        String joinedArgs = Arrays.stream(args)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(":"));
        return DigestUtils.sha256Hex(DigestUtils.sha256Hex(joinedArgs));
    }

    public static void setCookie(HttpServletResponse res, String jwt, boolean secureCookies) {
        // we need to use the native Undertow implementations as the support the SameSite anti-CSRF attributes
        Cookie cookie = createCookie(jwt, secureCookies);

        // spring wraps the response multiple times so we need to unwrap it in order to get the native implementation
        while (res instanceof HttpServletResponseWrapper) {
            res = (HttpServletResponse) ((HttpServletResponseWrapper) res).getResponse();
        }
        HttpServletResponseImpl nativeResponse = (HttpServletResponseImpl) res;
        cookie.setVersion(nativeResponse.getServletContext().getDeployment().getDeploymentInfo().getDefaultCookieVersion());
        nativeResponse.getExchange().setResponseCookie(cookie);
    }

    public static String createJwtToken(String userId, String secret, String hashClaim) {
        String jwt = Jwts.builder()
                .claim(HASH_CLAIM, hashClaim)
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        return jwt;
    }

    private static Cookie createCookie(String jwt, boolean secureCookies) {
        Cookie cookie = new CookieImpl(ACCESS_TOKEN_COOKIE_NAME, jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (EXPIRATION_TIME / MILLIS_PER_SECOND));
        cookie.setSecure(secureCookies); //ability to disable secure cookies for dev purposes
        cookie.setSameSite(true);
        cookie.setSameSiteMode("Lax");
        return cookie;
    }

    static LoginAuthenticationToken getAuthentication(HttpServletRequest request,
                HttpServletResponse response, String secret) {
        String token = null;
        if (request.getCookies() != null) {
            Optional<javax.servlet.http.Cookie> cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().endsWith(ACCESS_TOKEN_COOKIE_NAME))
                    .findFirst();
            if (cookie.isPresent()) {
                token = cookie.get().getValue();
            }
        }

        // if it's not found in a cookie, look in the Authorization header instead 
        if (StringUtils.isBlank(token)) {
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                token = new String(Base64.getDecoder().decode(authorization), StandardCharsets.UTF_8);
            }
        }
        
        token = token == null ? null : token.replace(TOKEN_PREFIX, "").trim();

        if (StringUtils.isNotBlank(token) && !request.getRequestURI().equalsIgnoreCase("/error")) {
            try {
                return createLoginAuthenticationToken(request, response, secret, token);
            } catch (JwtException ex) {
                logger.warn("Failed to parse token: {} in request for url {}",
                        ex.getMessage(), request.getRequestURI());
                logout(request, response);
            } catch (Exception ex) {
                logger.warn("Failed to authenticate {} {}", ex.getMessage(), request.getRequestURI());
                logout(request, response);
            }
        }
        return null;
    }

    public static LoginAuthenticationToken createLoginAuthenticationToken(
            HttpServletRequest request, HttpServletResponse response,
            String secret, String token) {

        // parse the token
        Jws<Claims> jwt = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);

        // verify algorithm
        if (!jwt.getHeader().getAlgorithm().equals(SignatureAlgorithm.HS512.getValue())) {
            logger.warn("Invalid JWT algorithm {}", jwt.getHeader().getAlgorithm());
            logout(request, response);
        }

        String userId = jwt
                .getBody()
                .getSubject();

        if (userId == null) {
            return null;
        }

        User user = null; // TODO userService.getUncachedUserDetailsById(UUID.fromString(userId));

        // we have to check if the fields email,password,2fa have changed and reject the token if so
        // this serves as an automatic revocation instead of password change, activating 2FA or email change
        String hashedValues = getHashClaim(user.getEmail(), user.getTwoFactorAuthSecret() != null, user.getPassword());

        if (!hashedValues.equals(jwt.getBody().get(HASH_CLAIM, String.class))) {
            return null;
        }
        return new LoginAuthenticationToken(user, null, 0);
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (javax.servlet.http.Cookie cookie : request.getCookies()) {
                if (cookie.getName() != null
                        && cookie.getName().equals(TokenAuthenticationService.ACCESS_TOKEN_COOKIE_NAME)) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
        // Spring security stores the security context in the session, so we need to invalidate it
        request.getSession().invalidate();
    }
}