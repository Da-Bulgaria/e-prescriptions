package bg.ehealth.prescriptions.rest;

import bg.ehealth.prescriptions.persistence.model.PharmacyView;
import bg.ehealth.prescriptions.services.pharmacy.PharmacyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PharmaciesController {

    private final PharmacyService service;

    public PharmaciesController(PharmacyService service) {
        this.service = service;
    }

    @GetMapping("/pharmacies")
    public ResponseEntity<List<PharmacyView>> getAllPharmacies() {
        return ResponseEntity.ok(service.allPharmacies());
    }
}

