package bg.ehealth.prescriptions.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import bg.ehealth.prescriptions.services.UserService;
import bg.ehealth.prescriptions.web.security.JWTAuthenticationFilter;
import bg.ehealth.prescriptions.web.security.JWTLoginFilter;

/**
 * Following this guide: https://auth0.com/blog/securing-spring-boot-with-jwts/
 */
@Configuration
@Order(5)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String APP_USER_URL = "/user/";
    private static final String SWAGGER_RESOURCES_URL = "/swagger-resources/";

    @Autowired
    private AuthenticationProvider postgresAuthenticationProvider;

    @Autowired
    private UserService userService;
    
    @Value("${secure.headers}")
    private boolean secureHeaders;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${reporturi.url.xss}")
    private String reportUriXss;

    @Value("${reporturi.url.expectct}")
    private String reportUriExpectCt;

    @Value("${reporturi.url.reportto}")
    private String reportUriReportTo;

    @Value("${reporturi.url.csp}")
    private String reportUriCsp;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeRequests()
            .antMatchers(APP_USER_URL + "**").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/webjars/**", "/api", "/api/", "/api-docs/**", SWAGGER_RESOURCES_URL + "**").permitAll()
            .antMatchers("/css/**", "/js/**", "/assets/**", "/images/**", "/favicon.ico", "/.well-known/**").permitAll()
            .antMatchers("/**").authenticated()
            .antMatchers("/adminpanel/**").authenticated()
            .and()
            .formLogin().loginPage("/login")
            .and()
            // We filter the api/login requests
            .addFilterBefore(new JWTLoginFilter(APP_USER_URL + "login", 
                    authenticationManager(), secureHeaders, jwtSecret, userService),
                    UsernamePasswordAuthenticationFilter.class)
            // And filter other app requests to check the presence of JWT in header
            .addFilterBefore(new JWTAuthenticationFilter(
                Arrays.asList(APP_USER_URL, SWAGGER_RESOURCES_URL, "/error"), jwtSecret), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // TODO consider stateless?
            .and().headers().contentSecurityPolicy((secureHeaders ? "upgrade-insecure-requests; " : "")
                + "script-src 'self' 'unsafe-inline' https://www.google-analytics.com/" + "; report-uri " + reportUriCsp)
            .and().referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
            .and().xssProtection().disable()
            .addHeaderWriter(new StaticHeadersWriter("X-Xss-Protection", "1; mode=block; report=" + reportUriXss))
            .addHeaderWriter(new StaticHeadersWriter("Report-To",
                "{'group':'default','max_age':31536000,'endpoints':[{'url':'"
                + reportUriReportTo + "'}],'include_subdomains':true}"))
            .addHeaderWriter(new StaticHeadersWriter("NEL",
                "{'report_to':'default','max_age':31536000,'include_subdomains':true"))
            .addHeaderWriter(new StaticHeadersWriter("Expect-CT", "max-age=604800, report-uri='" + reportUriExpectCt + "'"));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(postgresAuthenticationProvider);
    }
}