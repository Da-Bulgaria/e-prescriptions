package bg.ehealth.prescriptions.rest;

import bg.ehealth.prescriptions.services.icd.ICDEntity;
import bg.ehealth.prescriptions.services.icd.ICDService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ICDController {

    @Autowired
    private ICDService icdService;

    @GetMapping("/search")
    public List<ICDEntity> search(@RequestParam String keyword) throws IOException, ParseException {
        return icdService.search(keyword);
    }

    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam String lookup) throws IOException {
        return icdService.suggestTermsFor(lookup);
    }
}
