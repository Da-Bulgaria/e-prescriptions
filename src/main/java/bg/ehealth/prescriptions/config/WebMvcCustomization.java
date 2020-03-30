package bg.ehealth.prescriptions.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.escaper.EscapingStrategy;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.spring.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;

import bg.ehealth.prescriptions.web.security.WebUtils;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ExceptionHandler;

/**
 * Configuration for the WEB MVC component of spring mvc
 */
@Component
@Configuration
public class WebMvcCustomization implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcCustomization.class);

    @Value("${server.ssl.enabled}")
    private boolean useHttps;

    @Value("${environment}")
    private String environment;

    @Bean
    public SpringExtension pebbleSpringExtension() {
        return new SpringExtension();
    }

    @Bean
    public PebbleEngine pebbleEngine() {
        PebbleEngine.Builder builder = new PebbleEngine.Builder()
                .loader(new ClasspathLoader())
                .extension(pebbleSpringExtension())
                .addEscapingStrategy("jsFull", new JavascriptEscapeStrategy());

        if (environment.equals("dev")) {
            builder.cacheActive(false);
        }

        return builder.build();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

        PebbleViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setRedirectHttp10Compatible(false);
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setPebbleEngine(pebbleEngine());
        registry.viewResolver(viewResolver);
    }
  
    @Bean
    public UndertowServletWebServerFactory embeddedServletContainerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        factory.addBuilderCustomizers(
                builder -> {
                    if (useHttps) {
                        builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
                    }
                    builder.setServerOption(UndertowOptions.URL_CHARSET, "UTF-8");
                }
        );

        // Handling Spring's HttpFirewall exception
        factory.addDeploymentInfoCustomizers(new UndertowDeploymentInfoCustomizer() {
            @Override
            public void customize(DeploymentInfo deploymentInfo) {
                deploymentInfo.setExceptionHandler(
                        new CustomExceptionHandler(deploymentInfo.getExceptionHandler()));
            }
        });
        return factory;
    }

    @Bean
    public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        Map<String, ResourceHttpRequestHandler> mappings = new HashMap<>();
        mappings.put("**/apple-touch-icon-precomposed.png", appleFaviconRequestHandler());
        mappings.put("**/apple-touch-icon.png", appleFaviconRequestHandler());
        mappings.put("**/touch-icon-iphone.png", appleFaviconRequestHandler());
        mappings.put("**/apple-touch-icon-76x76-precomposed.png", appleFaviconRequestHandler());
        mappings.put("**/apple-touch-icon-76x76.png", appleFaviconRequestHandler());
        mappings.put("**/apple-touch-icon-120x120.png", appleFaviconRequestHandler());
        mappings.put("/favicon.ico", customFaviconRequestHandler());
        mappings.put("/assets/**", customAssetsRequestHandler());

        mapping.setUrlMap(mappings);
        return mapping;
    }

    @Bean
    public ResourceHttpRequestHandler customAssetsRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(Collections.singletonList(new ClassPathResource("assets/")));
        return requestHandler;
    }

    @Bean
    public ResourceHttpRequestHandler appleFaviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(Collections.singletonList(new ClassPathResource("assets/images/favicon.png")));
        return requestHandler;
    }

    @Bean
    public ResourceHttpRequestHandler customFaviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(Collections.singletonList(new ClassPathResource("assets/favicon.ico")));
        return requestHandler;
    }

    @Bean
    public AfterburnerModule jacksonAfterburner() {
        // optimization for eliminating data binding overhead
        return new AfterburnerModule();
    }

    /**
     * Extension for Pebble to add custom functions (e.g. formatDate)
     */
    public static final class JavascriptEscapeStrategy implements EscapingStrategy {

        @Override
        public String escape(String input) {
            return input.replace("\\", "\\u005C").replace("\t", "\\u0009")
                    .replace("\n", "\\u000A").replace("\f", "\\u000C")
                    .replace("\r", "\\u000D").replace("\"", "\\u0022")
                    .replace("%", "\\u0025").replace("&", "\\u0026")
                    .replace("'", "\\u0027").replace("/", "\\u002F")
                    .replace("<", "\\u003C").replace(">", "\\u003E");
        }
    }

    /**
     * Undertow Exception handler used to avoid logging the whole stacktrace of request rejections
     */
    public static class CustomExceptionHandler implements ExceptionHandler {
        private ExceptionHandler loggingHandler;

        public CustomExceptionHandler(ExceptionHandler loggingHandler) {
            this.loggingHandler = loggingHandler;
        }

        @Override
        public boolean handleThrowable(HttpServerExchange exchange, ServletRequest request,
                                       ServletResponse response, Throwable throwable) {
            if (throwable instanceof RequestRejectedException) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                logger.warn("Request rejected due to: {} for URI {}", throwable.getMessage(),
                        ((HttpServletRequest) request).getRequestURI());
                return true;
            } else if (loggingHandler != null) {
                return loggingHandler.handleThrowable(exchange, request, response, throwable);
            } else {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                String errorMessage = "Failed to process request: " + WebUtils.getRequestIp(httpRequest)
                        + " " + httpRequest.getRequestURL() + " " + httpRequest.getHeader("User-Agent");
                String exceptionMessage = ExceptionUtils.getMessage(throwable);
                if (exceptionMessage.contains("Broken pipe")
                        || exceptionMessage.contains("An established connection was aborted")) {
                    logger.warn(errorMessage + " due to Broken pipe or aborted connection");
                } else {
                    logger.error(errorMessage, throwable);
                }
                return true;
            }
        }
    }
}
