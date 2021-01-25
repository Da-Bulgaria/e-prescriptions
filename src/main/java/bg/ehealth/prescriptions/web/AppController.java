package bg.ehealth.prescriptions.web;

import bg.ehealth.prescriptions.persistence.model.enums.PrescriptionType;
import bg.ehealth.prescriptions.persistence.model.enums.UserType;
import bg.ehealth.prescriptions.web.security.LoginAuthenticationToken;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);
    
    private static final Set<String> IGNORED_PATHS = loadIgnoredPaths();
    
    @Autowired
    private PebbleEngine templateEngine;
    
    @RequestMapping(value = {"", "/", "/dashboard"}, method = RequestMethod.GET)
    public ModelAndView index(@AuthenticationPrincipal LoginAuthenticationToken token) {
        if (token.getUser().getUserType() == UserType.DOCTOR) {
            return new ModelAndView("doctor-dashboard");
        } else if (token.getUser().getUserType() == UserType.PHARMACIST) {
            return new ModelAndView("pharmacist-dashboard");
        }
        throw new IllegalStateException("Unsupported user type " + token.getUser().getUserType());
    }

    @GetMapping("/prescription")
    public ModelAndView prescription(@AuthenticationPrincipal LoginAuthenticationToken token) {
        ModelAndView mav = new ModelAndView("prescription");

        mav.addAllObjects(Map.of(
                "prescriptionTypes", PrescriptionType.values()
        ));

        return mav;
    }

    @RequestMapping(value = "/{path}", method = RequestMethod.GET)
    public ModelAndView htmlMapping(@PathVariable String path,
                                    @AuthenticationPrincipal LoginAuthenticationToken token,
                                    HttpServletResponse response) throws IOException {

        if (path.equals("login")) {
            return new ModelAndView("login");
        }
        
        // we first verify whether such a template exists. Many agents send requests to non-existent URLs
        // which results in long stacktraces that are useless. So we validate the existence of a template before actually
        // trying to render it
        try {
            templateEngine.getTemplate(path);
        } catch (PebbleException e) {
            if (!IGNORED_PATHS.contains(path)) {
                logger.warn("Failed to find template for path: /" + path);
            }
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new ModelAndView("error/404");
        }
        return new ModelAndView(path);
    }
    
    private static Set<String> loadIgnoredPaths() {
        InputStream in = AppController.class.getResourceAsStream("/misc/ignored-paths");
        if (in == null) {
            logger.warn("Failed to load ignored paths");
            return Collections.emptySet();
        }
        try {
            return new HashSet<>(IOUtils.readLines(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.warn("Failed to load ignored paths", e);
            return Collections.emptySet();
        }
    }

}
