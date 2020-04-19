package bg.ehealth.prescriptions.services.pharmacy;

import bg.ehealth.prescriptions.gateways.bda.BdaGateway;
import bg.ehealth.prescriptions.persistence.PharmacyRepository;
import bg.ehealth.prescriptions.persistence.model.Pharmacy;
import bg.ehealth.prescriptions.persistence.model.PharmacyView;
import bg.ehealth.prescriptions.services.pharmacy.excel.PharmacyExcelColumn;
import bg.ehealth.prescriptions.services.pharmacy.excel.PharmacyExcelSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PharmacyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PharmacyService.class);

    private final PharmacyExcelSheet excelSheet;
    private final BdaGateway bdaGateway;
    private final PharmacyRepository repository;

    public PharmacyService(PharmacyExcelSheet excelSheet,
                           BdaGateway bdaGateway,
                           PharmacyRepository repository) {
        this.excelSheet = excelSheet;
        this.bdaGateway = bdaGateway;
        this.repository = repository;
    }

    public List<PharmacyView> pharmacies() {
        return repository.findAllByOrderByCity();
    }

    public List<Pharmacy> seedPharmacies() {
        return excelSheet.pharmacies(bdaGateway.pharmacyRegistry(),
                PharmacyExcelColumn.valuesAsString())
                .stream()
                .filter(p -> !p.isEmpty())
                .map(p -> new Pharmacy(p.identifier(), p.name(), p.city(), p.address()))
                .collect(Collectors.toList());
    }

    public List<Pharmacy> saveAll(List<Pharmacy> pharmacies) {
        LOGGER.debug("Persisting {} pharmacies...", pharmacies.size());
        return repository.saveAll(pharmacies);
    }
}
