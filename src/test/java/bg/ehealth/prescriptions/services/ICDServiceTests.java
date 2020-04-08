package bg.ehealth.prescriptions.services;

import bg.ehealth.prescriptions.services.icd.ICDEntity;
import bg.ehealth.prescriptions.services.icd.ICDService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ICDServiceTests {

    @Autowired
    ICDService icdService;

    @Test
    public void testSearch() throws IOException, ParseException {
        List<ICDEntity> entities = icdService.search("Зависимост");
        assertEquals(10, entities.size());
        assertTrue(entities.get(0).getDescription().contains("Зависимост") ||
                entities.get(0).getDescription().contains("зависимост"));
    }

    @Test
    public void testAutocomplete() throws IOException, ParseException {
        List<String> autocompletes = icdService.suggestTermsFor("зав");
        assertEquals(5, autocompletes.size());
        assertTrue(autocompletes.contains("зависимост"));
    }
}
