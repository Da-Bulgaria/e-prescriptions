package bg.ehealth.prescriptions.services.pharmacy;

import bg.ehealth.prescriptions.gateways.BdaGateway;
import bg.ehealth.prescriptions.persistence.PharmacyRepository;
import bg.ehealth.prescriptions.persistence.model.Pharmacy;
import bg.ehealth.prescriptions.persistence.model.PharmacyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PharmacyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PharmacyService.class);

    private final PharmacyRegistry registry;
    private final BdaGateway bdaGateway;
    private final PharmacyRepository repository;

    public PharmacyService(PharmacyRegistry registry,
                           BdaGateway bdaGateway,
                           PharmacyRepository repository) {
        this.registry = registry;
        this.bdaGateway = bdaGateway;
        this.repository = repository;
    }

    public List<PharmacyView> allPharmacies() {
        return repository.findAllByOrderByCity();
    }

    public void importPharmacies() {
        List<Pharmacy> pharmacies = registry.pharmacies(bdaGateway.pharmacyRegistry(), PharmacyRegistryExcelColumn.valuesAsString()).stream()
                .filter(p -> !p.isEmpty())
                .map(p -> new Pharmacy(p.identifier(), p.name(), p.city(), p.address()))
                .collect(Collectors.toList());
        LOGGER.debug("Persisting pharmacies...");
        repository.saveAll(pharmacies);
        LOGGER.debug("done.");
    }
}
