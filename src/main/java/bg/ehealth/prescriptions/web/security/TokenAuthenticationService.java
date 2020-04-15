package bg.ehealth.prescriptions.web.security;

import bg.ehealth.prescriptions.persistence.model.User;
import bg.ehealth.prescriptions.persistence.model.enums.UserType;
import bg.ehealth.prescriptions.services.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.servlet.spec.HttpServletResponseImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that handles authentication using JSON web tokens (used by the back-office application)
 */
@Component
public class TokenAuthenticationService {

    private static final String USER_TYPE_CLAIM = "userType";
    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final Duration EXPIRATION_TIME = Duration.ofDays(5);
    private static final String TOKEN_PREFIX = "Bearer";
    public static final String HASH_CLAIM = "hash";

    public static void addAuthentication(HttpServletRequest req, HttpServletResponse res,
                                         User user, boolean secureCookies, String secret) {
        String jwt = createJwtToken(user.getId(), user.getUserType(), secret,
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

    public static String createJwtToken(String userId, UserType userType, String secret, String hashClaim) {
        return Jwts.builder()
                .claim(HASH_CLAIM, hashClaim)
                .claim(USER_TYPE_CLAIM, userType)
                .setSubject(userId)
                .setExpiration(Date.from(Instant.now().plusMillis(EXPIRATION_TIME.toMillis())))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)), SignatureAlgorithm.HS512)
                .compact();
    }

    private static Cookie createCookie(String jwt, boolean secureCookies) {
        Cookie cookie = new CookieImpl(ACCESS_TOKEN_COOKIE_NAME, jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) EXPIRATION_TIME.getSeconds());
        cookie.setSecure(secureCookies); //ability to disable secure cookies for dev purposes
        cookie.setSameSite(true);
        cookie.setSameSiteMode("Lax");
        return cookie;
    }

    static LoginAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                      String secret, UserService userService) {
        String token = null;

        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().endsWith(ACCESS_TOKEN_COOKIE_NAME))
                    .findFirst()
                    .map(javax.servlet.http.Cookie::getValue)
                    .orElse(null);
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
                return createLoginAuthenticationToken(request, response, secret, token, userService);
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
            String secret, String token, UserService userService) {

        // parse the token
        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)))
                .build()
                .parseClaimsJws(token);

        // verify algorithm
        if (!jwt.getHeader().getAlgorithm().equals(SignatureAlgorithm.HS512.getValue())) {
            logger.warn("Invalid JWT algorithm {}", jwt.getHeader().getAlgorithm());
            logout(request, response);
        }

        String userUin = jwt.getBody().getSubject();
        UserType userType = UserType.valueOf(jwt.getBody().get(USER_TYPE_CLAIM, String.class));
        
        if (userUin == null) {
            return null;
        }

        User user = userService.getUserByUin(userUin, userType);

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
